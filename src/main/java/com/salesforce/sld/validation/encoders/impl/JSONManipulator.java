/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.validation.encoders.impl;

import java.util.Set;

import com.salesforce.sld.validation.encoders.AbstractCharacterManipulator;

/**
 * JSONManipulator handles all content related to JavaScript Object Notation code
 */
public class JSONManipulator
    extends AbstractCharacterManipulator
{

    private Set<Character> escapeCharacters;

    protected JSONManipulator( Set<Character> escapeCharacters )
    {
        this.escapeCharacters = escapeCharacters;
    }

    @Override
    protected String getCorrectCharacter( Character c )
    {
        String correctedCharacter = "";

        // if the character is alphanumeric, it is OK
        if ( isAlphaNum( c ) )
        {
            correctedCharacter = String.valueOf( c );
        }
        // if the character should be escaped, do it
        // this disallows users from escaping JSON and writing HTML or JS code
        else if ( this.escapeCharacters.contains( c ) )
        {
            correctedCharacter = slashEscapeChar( c );
        }
        else if ( c > 0x9F )
        {
            correctedCharacter = String.valueOf( c );
        }
        // otherwise hex-encode and pad with \u0000
        else
        {
            String hex = getHexForCharacter( c );
            String pad = "0000".substring( hex.length() );
            correctedCharacter = "\\u" + pad + hex;
        }

        return correctedCharacter;
    }
}
