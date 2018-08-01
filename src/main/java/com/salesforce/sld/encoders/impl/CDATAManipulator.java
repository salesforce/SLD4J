/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.encoders.impl;

import java.io.IOException;

import com.salesforce.sld.encoders.AbstractManipulator;

/**
 * Handles all manipulations of CDATA sections.
 */
public class CDATAManipulator
    extends AbstractManipulator
{
    private static final Character CDATA_CONTROL_CHAR = ']';

    private static final Character CDATA_CONTROL_FINISH = '>';

    private static final String CDATA_ENCODED_APPEND = "]]>]]<![CDATA[>";

    @Override
    protected void encodeInternal( String input, Appendable output )
        throws IOException
    {
        handleString( input, output, false );
    }

    @Override
    protected void filterInternal( String input, Appendable output )
        throws IOException
    {
        handleString( input, output, true );
    }

    /**
     * Examines all characters in the input string for bad CDATA characters and the close CDATA string. Encodes or
     * removes the bad characters and replaces or removes the close CDATA strings
     * 
     * @param input a string to encode/filter
     * @param output the output destination object
     * @param shouldFilter true if filtering, false if encoding
     * @throws IOException should the append method fail
     */
    private void handleString( String input, Appendable output, boolean shouldFilter )
        throws IOException
    {
        char[] inputChars = input.toCharArray();
        int inputLength = inputChars.length;
        // inputIter is the index of the character we are about to look at
        for ( int inputIter = 0; inputIter < inputLength; inputIter++ )
        {
            char ch = inputChars[inputIter];

            // we've hit a ']' so start looking for more
            if ( ch == CDATA_CONTROL_CHAR )
            {
                // cdataIter controls the "buffer" size of ] characters, only 2 allowed
                int cdataIter = 0;
                char cdataCheck = ch;
                // read all the ] chars
                while ( cdataCheck == CDATA_CONTROL_CHAR )
                {
                    // check that we won't go out of bounds
                    if ( inputLength <= ( inputIter + cdataIter ) )
                    {
                        break;
                    }

                    cdataCheck = inputChars[inputIter + cdataIter];

                    if ( cdataIter > 2 )
                    {
                        // we've "buffered" 2 ] chars, so add a ] to the output and move the input iter up one
                        output.append( CDATA_CONTROL_CHAR );
                        inputIter++;
                    }
                    else
                    {
                        // we don't yet have 2 "buffered" ] chars, so try for another
                        cdataIter++;
                    }
                }

                if ( cdataCheck == CDATA_CONTROL_FINISH && cdataIter > 2 )
                {
                    // now we have an issue and need to fix it!

                    if ( !shouldFilter )
                    {
                        // if we're encoding, append the encoded version of cdata end
                        output.append( CDATA_ENCODED_APPEND );
                    }
                    // otherwise, do nothing, which removes the cdata end
                }
                else
                {
                    // we just had a bunch of ] chars in a row, no problem, play catch up with the main iterator
                    for ( int k = 0; k < cdataIter; k++ )
                    {
                        output.append( inputChars[inputIter + k] );
                    }
                }
                // update the iterator with the position of the cdata iterator
                inputIter = inputIter + cdataIter - 1;

            }
            // Control characters are always illegal
            else if ( ( ch <= 0x1f ) || // lower bounds of control characters except tab and newlines
                ( ch >= 0x7f && ch <= 0x84 ) || // DEL through APC control characters,
                ( ch >= 0x86 && ch <= 0x9f ) || // (still allows NEL character)
                ( ch >= 0xfdd0 && ch <= 0xfddf ) ) // more control chars
            {
                output.append( "" );
            }
            // all other characters are allowed
            else
            {
                output.append( ch );
            }

        }
    }
}
