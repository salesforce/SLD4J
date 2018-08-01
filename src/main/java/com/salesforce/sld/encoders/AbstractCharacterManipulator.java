/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.encoders;

import java.io.IOException;

/**
 * An abstract base for manipulators who only require single characters to manipulate. E.g. an encoder that doesn't need
 * to know the previous/next character to decide what to do with the current character.
 */
public abstract class AbstractCharacterManipulator
    extends AbstractManipulator
{

    @Override
    protected void encodeInternal( String input, Appendable output )
        throws IOException
    {
        for ( int i = 0; i < input.length(); i++ )
        {
            char c = input.charAt( i );
            output.append( getCorrectCharacter( c ) );
        }
    }

    @Override
    protected void filterInternal( String input, Appendable output )
        throws IOException
    {
        for ( int i = 0; i < input.length(); i++ )
        {
            Character c = input.charAt( i );
            String corr = getCorrectCharacter( c );
            if ( isSame( c, corr ) )
            {
                output.append( c );
            }
        }
    }

    /**
     * Given a character, do any defined, necessary modifications to the input string and return it
     * 
     * @param input a character to possibly modify
     * @return a result of a modification of the input character, or the input character as a string
     */
    protected abstract String getCorrectCharacter( Character input );

}
