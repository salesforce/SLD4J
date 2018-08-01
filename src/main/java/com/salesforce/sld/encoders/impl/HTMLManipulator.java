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
 * HTMLManipulator handles all content related to HTML
 */
public abstract class HTMLManipulator
    extends AbstractCharacterManipulator
{
    // These characters are allowed in any HTML Context, to be used in subclasses
    protected static final Set<Character> baseImmuneCharacters =
        Collections.unmodifiableSet( new HashSet<Character>( Arrays.asList( '!', '#', '$', '%', '^', '(', ')', '*', '+',
            ',', '-', '.', '/', ':', ';', '=', '?', '@', '[', '\\', ']', '_', '{', '|', '}', '~' ) ) );

    // for control characters, use the Replacement Character (? symbol in a diamond)
    private static final String REPLACE_HEX = "&#xfffd;";

    private Set<Character> immuneCharacters;

    protected HTMLManipulator( Set<Character> immuneCharacters )
    {
        this.immuneCharacters = immuneCharacters;
    }

    @Override
    protected String getCorrectCharacter( Character c )
    {
        String correctedCharacter = "";

        // if the character is alphanumeric, above the html control character set, or should be immune, it is OK
        if ( isAlphaNum( c ) || this.immuneCharacters.contains( c ) )
        {
            correctedCharacter = String.valueOf( c );
        }
        else
        {
            // Check if the character can be written as an entity to block attacks
            String entity = getEntityForCharacter( c );

            if ( entity != null )
            {
                correctedCharacter = entity;
            }
            // allow characters above the html control character set
            else if ( c > 0x9F )
            {
                correctedCharacter = String.valueOf( c );
            }
            // Otherwise, replace illegal control characters with a safe replacement
            // these characters have caused HTML parsing issues in some browsers in the past
            else if ( ( c <= 0x1f ) || // lower bounds of control characters
                ( c >= 0x7f && c <= 0x9f ) ) // DEL through APC control characters
            {
                correctedCharacter = REPLACE_HEX;
            }
            // RFC states to output illegal characters as hex replacements
            else
            {
                correctedCharacter = "&#x" + getHexForCharacter( c ) + ";";
            }
        }
        return correctedCharacter;
    }

    protected static String getEntityForCharacter( char entityChar )
    {
        String entity = null;
        switch ( entityChar )
        {
            case '"':
                entity = "&quot;";
                break;
            case '&':
                entity = "&amp;";
                break;
            case '<':
                entity = "&lt;";
                break;
            case '>':
                entity = "&gt;";
                break;
            case (char) 160:
                entity = "&nbsp;";
                break;
            default:
                break;
        }
        return entity;
    }

}
