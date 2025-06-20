/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.validation.encoders;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

/**
 * Base implementation of a Manipulator (handles both filtering and encoding). Provides common functionality for
 * character/string manipulations and bounds checking. Manipulators primarily implement "getCorrectCharacter" to
 * determine any Character changes necessary based on Manipulator details.
 */
public abstract class AbstractManipulator
{

    /**
     * A primary target for a Manipulator. filter removes any offending characters from the given string
     * 
     * @param input the string to filter
     * @return a filtered string based on the manipulator implementation or null, if the input is null
     */
    public String filter( String input )
    {
        if ( input == null )
        {
            return null;
        }

        StringBuilder sb = new StringBuilder( input.length() );

        try
        {
            filterInternal( input, sb );
        }
        catch ( IOException e )
        {
            // throw as unchecked as StringBuilder shouldn't have any IOExceptions
            throw new UncheckedIOException( e );
        }

        return sb.toString();
    }

    /**
     * A primary target for a Manipulator. filter removes any offending characters from the given string and writes to
     * the given Writer. If the provided input is null, no content is written to the Writer
     * 
     * @param input the string to filter
     * @param writer a Writer to write output to
     * @throws IOException if the writer throws an IOException
     * @throws IllegalArgumentException if the writer is null
     */
    public void filter( String input, Writer writer )
        throws IOException, IllegalArgumentException
    {
        if ( input == null )
        {
            return;
        }

        if ( writer == null )
        {
            throw new IllegalArgumentException( "Writer cannot be null" );
        }

        filterInternal( input, writer );

    }

    /**
     * A primary target for a Manipulator. encode modifies offending characters to their "safe" equivalents
     * 
     * @param input the string to encode
     * @return an encoded string based on the manipulator implementation or null, if the input is null
     */
    public String encode( String input )
    {
        if ( input == null )
        {
            return null;
        }

        // length * 3 is a best guess
        StringBuilder sb = new StringBuilder( input.length() * 3 );
        try
        {
            encodeInternal( input, sb );
        }
        catch ( IOException e )
        {
            // throw as unchecked as StringBuilder shouldn't have any IOExceptions
            throw new UncheckedIOException( e );
        }
        return sb.toString();
    }

    /**
     * A primary target for a Manipulator. encode modifies offending characters to their "safe" equivalents and writes
     * to the given Writer. If the provided input is null, no content is written to the Writer
     * 
     * @param input the string to encode
     * @param writer a Writer to write output to
     * @throws IOException if the writer throws an IOException
     * @throws IllegalArgumentException if the writer is null
     */
    public void encode( String input, Writer writer )
        throws IOException, IllegalArgumentException
    {
        if ( input == null )
        {
            return;
        }

        if ( writer == null )
        {
            throw new IllegalArgumentException( "Writer cannot be null" );
        }

        encodeInternal( input, writer );
    }

    /**
     * An aid to filtering, isSame returns true if a given Character exactly matches a given String in size (1) and
     * content
     * 
     * @param c a character to test against
     * @param s a string to compare to the character
     * @return true if the string describes the character exactly
     */
    public final static boolean isSame( Character c, String s )
    {
        // length is checked as a shortcut and sanity check against
        // "&" -> "&amp;" being equal since their first characters are equal
        return s.length() == 1 && c.equals( s.charAt( 0 ) );
    }

    /**
     * Converts given character to it's escaped version
     * 
     * @param c a character to possibly escape
     * @return a slash-escaped version of the character, if necessary
     */
    public final static String slashEscapeChar( Character c )
    {
        String value = null;
        switch ( c )
        {
            case '\t':
                value = "\\t";
                break;
            case '\b':
                value = "\\b";
                break;
            case '\n':
                value = "\\n";
                break;
            case '\r':
                value = "\\r";
                break;
            case '\f':
                value = "\\f";
                break;
            default:
                value = "\\" + String.valueOf( c );
                break;
        }

        return value;
    }

    /**
     * Checks to see if a character is alphanumeric
     * 
     * @param c a character to check against
     * @return true if the character is in the set of lowercase, uppercase, or numeric characters, false otherwise
     */
    public final static boolean isAlphaNum( char c )
    {
        return Character.isLetterOrDigit( c );
    }

    /**
     * Given a char, return the Hex representation of that char (does not include 0x or similar)
     * 
     * @param c a character to hexify
     * @return the hex string representation of the character
     */
    public final static String getHexForCharacter( char c )
    {
        return Integer.toHexString( c );
    }

    /**
     * Given a character, do any defined, necessary encodings to the input string and append it to the output object
     * 
     * @param input the string to encode
     * @param output the object to append the encoded version of the string to
     * @throws IOException if any IOExceptions occur in the subclass
     */
    protected abstract void encodeInternal( String input, Appendable output )
        throws IOException;

    /**
     * Given a character, do any defined, necessary filterings to the input string and append it to the output object
     * 
     * @param input the string to encode
     * @param output the object to append the encoded version of the string to
     * @throws IOException if any IOExceptions occur in the subclass
     */
    protected abstract void filterInternal( String input, Appendable output )
        throws IOException;

}
