/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.validation.csrf;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>
 * The CSRFTokenManager is in charge of generating and validating CSRF tokens. This CSRF defense allows applications to
 * generate <a href="http://www.corej2eepatterns.com/Design/PresoDesign.htm"> synchronizer tokens</a> as recommended by
 * <a href= "https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)_Prevention_Cheat_Sheet"> OWASP</a>. These
 * tokens are semi-stateless (tied to session, but not stored anywhere) and so can be generated wherever in the
 * application and validated wherever.
 * </p>
 * <p>
 * In the default case, CSRF tokens are generated in this manner: <br>
 * </p>
 * <ol>
 * <li>Given a SessionID (must be at least 16 bytes long), first generate a TokenID from a {@linkplain SecureRandom}
 * </li>
 * <li>Next, get the current timestamp</li>
 * <li>Create the text to be encrypted, cryptText, as SessionID|timestamp</li>
 * <li>Use the TokenID as an IV and the first 16 bytes of the SessionID as key to encrypt the cryptText</li>
 * <li>Then prepend the TokenID to the encrypted value. This is the Token</li>
 * </ol>
 * <p>
 * In the default case, CSRF tokens are validated in this manner: <br>
 * </p>
 * <ol>
 * <li>Given a SessionID and a Token, first split the token into TokenID and encrypted text</li>
 * <li>Next, decrypt the encrypted text using the first 16 bytes of the SessionID as key and TokenID as IV</li>
 * <li>Split the resulting cryptText into SessionID and timestamp</li>
 * <li>validate that the full SessionID matches the decrypted version (not just first 16 bytes)</li>
 * <li>validate that the timestamp is within the expiration time from now</li>
 * </ol>
 * <p>
 * The generation and validation methods also allow application developers to specify further items to be encrypted (in
 * addition to the sessionID and timestamp). These items are validated in their supplied order, so developers should
 * take care to maintain the same order during validation as was used in generation.
 * </p>
 * <p>
 * <b>Note:</b> By default, a {@linkplain ICSRFErrorHandler} is assigned to the Manager. This handler writes all
 * messages to SysErr (or throws exceptions if an exception occurs) during generation and validation when an error has
 * occurred or validation has failed. Implementors should override this behavior to log CSRF token validation errors to
 * log correlation/SIEM or some metrics system to track potentially malicious activity
 * </p>
 *
 * @author Chris Smith
 */
public class StatelessCSRFTokenManager
{

    /*
     * 30 minutes in milliseconds
     */
    public static final long DEFAULT_EXPIRY = 30 * 60 * 1000;

    public static final String DEFAULT_CSRF_TOKEN_NAME = "csrf_token";

    /*
     * size of the generated token's ID (not the final token). it must be 16
     * bytes long at least
     */
    private static final int TOKEN_SIZE = 16;

    /*
     * Default required for AES/GCM
     */
    private static final int KEY_SIZE = 16;

    /*
     * Default required for AES/GCM
     */
    private static final int GCM_TAG_BITS = 128;

    /*
     * Default required for AES/GCM
     */
    private static final int PARAMETER_SPEC_SIZE = 16;

    private static final String SEPARATOR = "|";

    /////////////////////
    // Member variables
    /////////////////////

    /*
     * a strong random to use to generate Token IDs to be used as IVs 
     */
    private SecureRandom random;

    /*
     * Stores a token name only as a convenience
     */
    private String csrfTokenName;

    /*
     * Time in milliseconds for the token to persist
     */
    private long expiry;

    /*
     * manages all error conditions. Default is to write to syserr/throw an exception for fatal errors
     */
    private ICSRFErrorHandler handler;

    /**
     * Create a new {@linkplain StatelessCSRFTokenManager} with all defaults
     */
    public StatelessCSRFTokenManager()
    {
        this( null, null );
    }

    /**
     * Create a new {@linkplain StatelessCSRFTokenManager} with all defaults and use the provided
     * {@linkplain SecureRandom}
     * 
     * @param random a {@linkplain SecureRandom} instance to use in generating random tokens or null which will generate
     *            a new {@linkplain SecureRandom}
     */
    public StatelessCSRFTokenManager( SecureRandom random )
    {
        this( random, null );
    }

    /**
     * Create a new {@linkplain StatelessCSRFTokenManager} with all defaults and use the provided
     * {@linkplain SecureRandom} and {@linkplain ICSRFErrorHandler}
     * 
     * @param random a {@linkplain SecureRandom} instance to use in generating random tokens or null which will generate
     *            a new {@linkplain SecureRandom}
     * @param handler a {@linkplain ICSRFErrorHandler} instance to handle reporting issues that are raised during
     *            processing, or null which will default to the {@linkplain DefaultCSRFErrorHandler} instead
     */
    public StatelessCSRFTokenManager( SecureRandom random, ICSRFErrorHandler handler )
    {
        if ( random == null )
        {
            random = new SecureRandom();
        }
        this.random = random;

        if ( handler == null )
        {
            handler = new DefaultCSRFErrorHandler();
        }
        this.handler = handler;

        this.csrfTokenName = DEFAULT_CSRF_TOKEN_NAME;
        this.expiry = DEFAULT_EXPIRY;
    }

    /**
     * Configure this object to use a different {@linkplain ICSRFErrorHandler}
     * 
     * @param handler the {@linkplain ICSRFErrorHandler} to use
     * @throws IllegalArgumentException if the handler is null
     */
    public void setErrorHandler( ICSRFErrorHandler handler )
        throws IllegalArgumentException
    {
        if ( handler == null )
        {
            throw new IllegalArgumentException( "Provided handler is null" );
        }

        this.handler = handler;
    }

    /**
     * Returns the assigned name for CSRF tokens
     *
     * @return CSRF Token parameter name
     */
    public String getCSRFTokenName()
    {
        return this.csrfTokenName;
    }

    /**
     * Configure a new token name. This is a convenience method
     * 
     * @param tokenName the new name for the token
     * @throws IllegalArgumentException if the tokenName is null
     */
    public void setCSRFTokenName( String tokenName )
        throws IllegalArgumentException
    {
        if ( tokenName == null )
        {
            throw new IllegalArgumentException( "Provided CSRF Token name is null" );
        }

        this.csrfTokenName = tokenName;
    }

    /**
     * Returns the expiration time in milliseconds for CSRF Tokens
     *
     * @return CSRF Token parameter expiration in millis
     */
    public long getAllowedExpiry()
    {
        return this.expiry;
    }

    /**
     * Configure a new expiration time on tokens. This takes effect immediately on all outstanding tokens. e.g. if the
     * old expiry were 10 mins and the new expiry is 20 mins, all tokens generated 19 mins ago are now valid, even
     * though they weren't before the expiration was reset.
     * 
     * @param expiry the new expiration time in milliseconds
     * @throws IllegalArgumentException if the expiration time is less that 0
     */
    public void setAllowedExpiry( long expiry )
        throws IllegalArgumentException
    {
        if ( expiry < 0L )
        {
            throw new IllegalArgumentException( "Provided token expiration is negative" );
        }

        this.expiry = expiry;
    }

    /**
     * Builds a secure token used to protect against CSRF attacks. Additional data may be used to further lengthen the
     * resulting token
     *
     * @param sessionID the sessionID of this request
     * @param otherData (Optional) any other strings that should be used to generate the token. See class definition.
     * @return a new CSRF token value for this session
     * @throws IllegalArgumentException if the sessionID is null
     */
    public String generateToken( String sessionID, String... otherData )
        throws IllegalArgumentException
    {
        if ( sessionID == null )
        {
            throw new IllegalArgumentException( "Token cannot be generated from null sessionID" );
        }

        if ( sessionID.length() < KEY_SIZE )
        {
            this.handler.handleInternalError( "Token cannot be generated from session size less than " + KEY_SIZE );
            return null;
        }

        byte[] tokenId = generateID( TOKEN_SIZE );

        byte[] token = generateTokenInternal( tokenId, sessionID, otherData );

        byte[] finalBytes = new byte[tokenId.length + token.length];
        System.arraycopy( tokenId, 0, finalBytes, 0, tokenId.length );
        System.arraycopy( token, 0, finalBytes, tokenId.length, token.length );

        String finalToken = encodeToken( finalBytes );

        return finalToken;
    }

    /**
     * Generate a random byte array of some given size
     *
     * @param size the number of bytes the ID should contain
     * @return a random byte array
     */
    private byte[] generateID( int size )
    {
        byte[] bytes = new byte[size];
        random.nextBytes( bytes );
        return bytes;
    }

    /**
     * Generate a key based on a random id, session id, and current time with optional other data added
     *
     * @param id a random id
     * @param sessionID the session of the current request
     * @param dataToCrypt (Optional) any other strings that should be used to generate the token. See class definition.
     * @return a generated stateless token, or null, if an error occurred
     */
    private byte[] generateTokenInternal( byte[] id, String sessionID, String... dataToCrypt )
    {
        String timestamp = Long.toString( getCurrentTime() );

        // always begin with session and timestamp
        StringBuilder sbCryptText = new StringBuilder();
        sbCryptText.append( sessionID ).append( SEPARATOR ).append( timestamp );

        // if other data supplied, append it to the sbCryptText in the same format, maintaining ordering
        if ( dataToCrypt != null )
        {
            int len = dataToCrypt.length;
            for ( int i = 0; i < len; i++ )
            {
                sbCryptText.append( SEPARATOR ).append( dataToCrypt[i] );
            }
        }

        String cryptText = sbCryptText.toString();
        byte[] key = sessionID.getBytes( Charset.defaultCharset() );
        byte[] iv = id;

        byte[] encryptedValue = null;
        try
        {
            encryptedValue = crypt( key, iv, cryptText.getBytes( "UTF-8" ), Cipher.ENCRYPT_MODE );
        }
        catch ( Exception e )
        {
            String error = new StringBuilder().append( "CSRF Token generation failed for tokenID " ).append( id )
                .append( ", and sessionID " ).append( sessionID ).append( " with exception" ).toString();

            this.handler.handleFatalException( error, e );
        }

        return encryptedValue;
    }

    /**
     * Ensures that the supplied token is a valid csrf token. Valid tokens are made up of the current session, randomly
     * generated token, timestamp, and any other data supplied. The timestamp must be within some number of milliseconds
     * before now based on the {@linkplain #getAllowedExpiry()}
     *
     * @param token the incoming token to test against
     * @param sessionID the sessionID of the current request
     * @param otherData (Optional) any other strings that should be used to validate the token. See class definition.
     * @return true if the token is valid for this sessionID. false otherwise
     * @throws IllegalArgumentException if the session ID is null
     */
    public boolean validateToken( String token, String sessionID, String... otherData )
        throws IllegalArgumentException
    {
        if ( token == null || token.length() == 0)
        {
            this.handler.handleInternalError( "CSRF token does not exist" );
            return false;
        }

        if ( sessionID == null )
        {
            throw new IllegalArgumentException( "Provided session id is null" );
        }

        boolean isValid = validateTokenInternal( token, sessionID, otherData );

        if ( !isValid )
        {
            this.handler.handleValidationError( "Could not validate CSRF token. CSRF attack detected" );
        }

        return isValid;
    }

    /**
     * Tests the given token id + string for validity. Also does internal checking of string to attempt to detect
     * tampering
     *
     * @param token the random ID to use in key generation
     * @param sessionID the session of the current request
     * @param dataToCrypt (Optional) any other strings that should be used to validate the token. See class definition.
     * @return true if the token is valid, false otherwise
     */
    private boolean validateTokenInternal( String token, String sessionID, String... dataToCrypt )
    {
        boolean result = false;

        long timestamp = getCurrentTime();

        try
        {
            byte[] key = sessionID.getBytes( Charset.defaultCharset() );

            byte[] tokenByte = decodeToken( token );
            if( tokenByte.length < TOKEN_SIZE )
            {
                String error = new StringBuffer().append("The format of the CSRF token received is incorrect")
                    .toString();

                this.handler.handleValidationError( error );
                return result;
            }
            byte[] iv = Arrays.copyOfRange( tokenByte, 0, TOKEN_SIZE );
            byte[] encryptedValue = Arrays.copyOfRange( tokenByte, TOKEN_SIZE, tokenByte.length );

            byte[] decrypted = crypt( key, iv, encryptedValue, Cipher.DECRYPT_MODE );
            String cryptText = new String( decrypted, "UTF-8" );
            String[] decryptParts = cryptText.split( Pattern.quote( SEPARATOR ) );

            int cryptlen = dataToCrypt == null ? 0 : dataToCrypt.length;

            // 2 guaranteed pieces (session and timestamp) plus the additional data
            if ( decryptParts.length == ( 2 + cryptlen ) )
            {
                String decryptedSession = decryptParts[0];
                long decryptedTimestamp = Long.parseLong( decryptParts[1] );

                /*
                 * verify sessions match verify that the timestamp in the 
                 * token is within the permitted time allowance and verify 
                 * all other possible data matches in order
                 */
                if ( !decryptedSession.equals( sessionID ) )
                {
                    String error = new StringBuilder().append( "CSRF Token session ids don't match. Expected: " )
                        .append( sessionID ).append( "but received: " ).append( decryptedSession ).toString();

                    this.handler.handleValidationError( error );
                }
                else if ( ( decryptedTimestamp + getAllowedExpiry() ) < timestamp )
                {
                    String error = new StringBuilder().append( "CSRF Token has expired. Expected: " )
                        .append( timestamp ).append( " but received: " ).append( decryptedTimestamp ).toString();

                    this.handler.handleValidationError( error );
                }
                else if ( cryptlen > 0 )
                {
                    for ( int i = 0; i < cryptlen; i++ )
                    {
                        String decryptedData = decryptParts[2 + i];
                        String intendedData = dataToCrypt[i];
                        if ( decryptedData.equals( intendedData ) )
                        {
                            result = true;
                        }
                        else
                        {
                            String error = new StringBuilder().append( "CSRF Token data does not match. Excepted: " )
                                .append( intendedData ).append( " but received: " ).append( decryptedData ).toString();

                            this.handler.handleValidationError( error );

                            result = false;

                            // if any fails, quit immediately
                            break;
                        }
                    }
                }
                else
                {
                    result = true;
                }
            }
        }
        catch ( AEADBadTagException e )
        {
            String error = new StringBuilder().append( "Could not validate token " ).append( token )
                .append( " for different session " ).append( sessionID ).toString();

            this.handler.handleValidationError( error );
        }
        catch ( Exception e )
        {
            String error =
                new StringBuilder().append( "Could not validate token " ).append( token ).append( " for session " )
                    .append( sessionID ).append( " due to exception: " ).append( e.getMessage() ).toString();

            this.handler.handleFatalException( error, e );
        }

        return result;
    }

    /**
     * encrypts or decrypts using AES/GCM and the given values
     *
     * @param key the key to use with *crypting
     * @param iv the iv to use when *crypting
     * @param textBytes the encrypted value to be decrypted OR the plaintext to be encrypted
     * @param mode either {@link Cipher#ENCRYPT_MODE} or {@link Cipher#DECRYPT_MODE}
     * @return the encrypted or decrypted value, depending on the given mode
     * @throws NoSuchAlgorithmException if the underlying code throws this exception
     * @throws NoSuchPaddingException if the underlying code throws this exception
     * @throws InvalidKeyException if the underlying code throws this exception
     * @throws InvalidAlgorithmParameterException if the underlying code throws this exception
     * @throws IllegalBlockSizeException if the underlying code throws this exception
     * @throws BadPaddingException if the underlying code throws this exception
     * @throws UnsupportedEncodingException if the underlying code throws this exception
     */
    private byte[] crypt( byte[] key, byte[] iv, byte[] textBytes, int mode )
        throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
        InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
        byte[] cryptedValue = null;
        Cipher cipher = Cipher.getInstance( "AES/GCM/NoPadding" );
        SecretKeySpec keyspec = new SecretKeySpec( key, 0, KEY_SIZE, "AES" );
        GCMParameterSpec gcmspec = new GCMParameterSpec( GCM_TAG_BITS, iv, 0, PARAMETER_SPEC_SIZE );
        cipher.init( mode, keyspec, gcmspec );
        cryptedValue = cipher.doFinal( textBytes );
        return cryptedValue;
    }

    /**
     * Return the current time for this application, in milliseconds. This method is exposed to subclasses as system
     * clocks may vary in multinode cloud environments that may span normal datetimes.
     * 
     * @return the current time in milliseconds
     */
    protected long getCurrentTime()
    {
        return System.currentTimeMillis();
    }

    /**
     * Given a byte array, return an encoded version using a URL-safe Base64.
     * 
     * @param tokenBytes the bytes of a csrf token
     * @return an encoded String of the tokenBytes
     */
    protected String encodeToken( byte[] tokenBytes )
    {
        return Base64.getUrlEncoder().encodeToString( tokenBytes );
    }

    /**
     * Given a CSRF token String, return an decoded version using a URL-safe Base64.
     * 
     * @param tokenString the csrf token String
     * @return the decoded bytes of the tokenString
     */
    protected byte[] decodeToken( String tokenString )
    {
        return Base64.getUrlDecoder().decode( tokenString );
    }

}
