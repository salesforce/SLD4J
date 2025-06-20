/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.validation.encoders.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.salesforce.sld.validation.encoders.AbstractCharacterManipulator;

/**
 * XMLManipulator handles all content related to XML
 */
public class XMLManipulator
    extends AbstractCharacterManipulator
{
    // These characters are allowed in any XML Context, to be used in subclasses
    protected static final Set<Character> baseImmuneCharacters = Collections.unmodifiableSet(
        new HashSet<Character>( Arrays.asList( ',', ';', ':', '.', '_', ' ', '(', ')', '\t', '\n', '\r' ) ) );

    private static final Map<Character, String> characterToEntityMap = createEntityMap();

    private static final String REPLACE_HEX = ""; // for control characters, use blank, from RFC

    private Set<Character> immuneCharacters;

    protected XMLManipulator( Set<Character> immuneCharacters )
    {
        this.immuneCharacters = immuneCharacters;
    }

    @Override
    protected String getCorrectCharacter( Character c )
    {
        String correctedCharacter = "";

        // If the character is alphanumeric or is immune, it is OK
        if ( isAlphaNum( c ) || this.immuneCharacters.contains( c ) )
        {
            correctedCharacter = String.valueOf( c );
        }
        else
        {
            // Check if the character can be written as an entity to block attacks
            String entity = characterToEntityMap.get( c );

            if ( entity != null )
            {
                correctedCharacter = entity;
            }
            // Otherwise, replace illegal control characters with a safe replacement
            // these characters can have special meaning and are recommended to be removed by the RFC
            else if ( ( c <= 0x1f ) || // lower bounds of control characters except tab and newlines
                ( c >= 0x7f && c <= 0x84 ) || // DEL through APC control characters,
                ( c >= 0x86 && c <= 0x9f ) || // (still allows NEL character)
                ( c >= 0xfdd0 && c <= 0xfddf ) ) // more control chars
            {
                correctedCharacter = REPLACE_HEX;
            }
            else if ( c > 0x9F )
            {
                correctedCharacter = String.valueOf( c );
            }
            // Otherwise encode the character in hex
            else
            {
                correctedCharacter = "&#x" + getHexForCharacter( c ) + ";";
            }
        }
        return correctedCharacter;
    }

    /**
     * Small unmodifiable map of entity mappings
     * 
     * @return
     */
    private static Map<Character, String> createEntityMap()
    {
        Map<Character, String> map = new HashMap<Character, String>( 4 );
        map.put( (char) 34, "&quot;" ); /* quotation mark */
        map.put( (char) 38, "&amp;" ); /* ampersand */
        map.put( (char) 39, "&apos;" ); /* single quote*/
        map.put( (char) 60, "&lt;" ); /* less-than sign */
        map.put( (char) 62, "&gt;" ); /* greater-than sign */
        return Collections.unmodifiableMap( map );
    }

}
