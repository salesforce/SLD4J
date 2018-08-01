/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.sld.encoders.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.salesforce.sld.encoders.AbstractCharacterManipulator;

/**
 * JavaScriptManipulator handles all content related to JavaScript code
 */
public abstract class JavaScriptManipulator
    extends AbstractCharacterManipulator
{
    // These characters must always be slash escaped
    protected static final Set<Character> baseEscapeList =
        Collections.unmodifiableSet( new HashSet<Character>( Arrays.asList( '\b', '\t', '\n', '\f', '\r', '\\' ) ) );

    // These characters are always allowed
    protected static final Set<Character> baseIgnoreList =
        Collections.unmodifiableSet( new HashSet<Character>( Arrays.asList( '~', '!', '@', '#', '%', '^', '*', '(', ')',
            '_', '+', '=', '|', '[', ']', ':', ';', '<', '>', '?', ',', '.', '-', '/', ' ' ) ) );

    private Set<Character> escapeCharacters;

    private Set<Character> ignoreCharacters;

    protected JavaScriptManipulator( Set<Character> ignoreCharacters, Set<Character> escapeCharacters )
    {
        this.ignoreCharacters = ignoreCharacters;
        this.escapeCharacters = escapeCharacters;
    }

    @Override
    protected String getCorrectCharacter( Character c )
    {
        String correctedCharacter = "";

        // if the character is alphanumeric it is OK
        if ( isAlphaNum( c ) )
        {
            correctedCharacter = String.valueOf( c );
        }
        // if the character should be escaped, escape it
        else if ( this.escapeCharacters.contains( c ) )
        {
            correctedCharacter = slashEscapeChar( c );
        }
        // if the character should be ignored, do
        // this happens after escaping, as a character must be escaped instead of ignored
        // if it is in both lists, see '-'
        else if ( this.ignoreCharacters.contains( c ) )
        {
            correctedCharacter = String.valueOf( c );
        }
        // if the character is above the basic latin character set it is OK
        else if ( c > 0x7f )
        {
            correctedCharacter = String.valueOf( c );
        }
        else
        {
            // Now get the hex representation of the character and pad it
            String hex = getHexForCharacter( c );

            String pad;

            // js pads ASCII under 128 as \x00 padded
            if ( c < 128 )
            {
                pad = "00".substring( hex.length() );
                correctedCharacter = "\\x" + pad + hex;
            }
            // js pads Unicode 128+ as \u0000 padded, but we allow these characters as-is
            // else
            // {
            // pad = "0000".substring( hex.length() );
            // correctedCharacter = "\\u" + pad + hex;
            // }

        }

        return correctedCharacter;
    }
}
