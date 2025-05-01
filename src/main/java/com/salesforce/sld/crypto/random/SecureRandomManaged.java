package com.salesforce.sld.crypto.random;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;

/**
 * SecureRandomManaged manages the lifecycle of SecureRandom. This implementation
 * reseeds values every-so-often to maintain entropy "freshness".
 * 
 * @author Chris Smith
 */
public class SecureRandomManaged
    extends SecureRandom
{
    private static final long serialVersionUID = -4556215599767928821L;

    // 1 million uses
    private static final int COUNTDOWN_MAX = 1000000;

    // 1 hundred uses if manually set
    private static final int COUNTDOWN_MANUAL = 100;

    // More than enough bytes of seed data
    private static final int SEED_SIZE = 128;

    // When countdown reaches 0, reseed and reset countdown
    private int countdown;

    /*
     * Use a second SR to manage seed generation. The output should never be
     * exposed externally
     */
    private final SecureRandom seedGenerator;

    /**
     * Construct a new managed SecureRandom instance
     */
    public SecureRandomManaged()
    {
        super();
        this.seedGenerator = createSeedGenerator();
        configureSystemProperties();
        setNewSeed();
        resetCountdown( COUNTDOWN_MAX );
    }

    /**
     * Configures the system property java.security.egd to use the /dev/urandom
     * entropy for {@linkplain SecureRandom}s as a secondary protection against
     * blocking
     */
    private void configureSystemProperties()
    {
        /**         
         * On Linux this property needs to be set to avoid blocking on
         * SecureRandom#generateSeed.
         */
        if ( System.getProperty( "java.security.egd" ) == null
            && System.getProperty( "os.name" ).toLowerCase( Locale.ROOT ).startsWith( "linux" ) )
        {
            System.setProperty( "java.security.egd", "file:/dev/../dev/urandom" );
        }
    }

    /**
     * Construct a secondary {@linkplain SecureRandom} to use as a seed
     * generator. This is so infrequently used, we don't need to worry about
     * reseeding. It is also never externally exposed, so constructing attacks
     * is incredibly difficult, if not impossible.
     */
    private SecureRandom createSeedGenerator()
    {
        SecureRandom seeder;
        try
        {
        	/**
        	 * Linux/MacOS/Solaris implementation only. Forces the usage of non-blocking SR to seed.
        	 */
            seeder = SecureRandom.getInstance( "NativePRNGNonBlocking" );
        }
        catch ( NoSuchAlgorithmException e )
        {
        	/**
        	 * Windows implementation. NativePRNG is not implemented.
        	 * Uses non-blocking native implementation from SunMSCAPI provider.
        	 */
            seeder = new SecureRandom();
        }

        return seeder;
    }

    /**
     * Supplement the seed with a newly seeded value instead of constructing a
     * new object. This is Cryptographically strong.
     */
    private void setNewSeed()
    {
        byte[] seed = new byte[SEED_SIZE];
        this.seedGenerator.nextBytes( seed );
        super.setSeed( seed );
        resetCountdown( COUNTDOWN_MAX );
    }

    /**
     * Sets the countdown to the specific value, or creates it, if need be.
     * 
     * @param countdown the value to reset/set the countdown to.
     */
    private void resetCountdown( int countdown )
    {
        this.countdown = countdown;
    }

    /**
     * Reseed if the specified countdown value is approaching.
     */
    private void checkReseed()
    {
        if ( this.countdown-- <= 0 )
        {
            setNewSeed();
        }
    }

    /**
     * Reseeds this random object. 
     * The given seed supplements, rather than replaces, the existing seed. 
     * Thus, repeated calls are guaranteed never to reduce randomness.
     * 
     * @param seed 
     * 			Reseeds this random object
     */
    @Override
    public void setSeed( byte[] seed )
    {
        super.setSeed( seed );
        resetCountdown( Math.min( COUNTDOWN_MANUAL , this.countdown ));
    }

    /**
     * Reseeds this random object. 
     * The given seed supplements, rather than replaces, the existing seed. 
     * Thus, repeated calls are guaranteed never to reduce randomness.
     * 
     * @param seed 
     * 			Reseeds this random object, using the eight bytes contained in the given long seed
     */
    @Override
    public void setSeed( long seed )
    {
        super.setSeed( seed );
        resetCountdown( Math.min( COUNTDOWN_MANUAL , this.countdown ));
    }
    
    /**
     * Prevents the usage of generateSeed to generate a seed and 
     * instead must be used to generate a PSRN. 
     * <p>
     * This is to avoid reducing the entropy "freshness" of the seed 
     * which may occur if generateSeed is called multiple times.
     * <p>
     * Switches to nextBytes to bypass blocking super's generateSeed method
     * blocks on a file read, this version never blocks. We are not exposing
     * this object's seed generator to the outside world to maintain it's
     * security. Otherwise, we'd have to reseed the seed generator and it
     * becomes a recursive issue quickly
     * 
     * @param numBytes
     * 			the required number of random bytes
     * @return nextBytes 
     * 			random bytes of user specified number
     */
    @Override
    public byte[] generateSeed( int numBytes )
    {

        byte[] nextBytes = new byte[numBytes];
        nextBytes( nextBytes );
        return nextBytes;
    }

    /**
     * Generates a user-specified number of random bytes.
     * <p>
     * Checks and reseeds if the countdown value is approaching "0".
     * This improves entropy.
     * 
     * @param bytes
     * 			the required number of random bytes
     */
    @Override
    public void nextBytes( byte[] bytes )
    {
        // Reseed after every few uses to maintain long term security.
        checkReseed();
        super.nextBytes( bytes );
    }

}