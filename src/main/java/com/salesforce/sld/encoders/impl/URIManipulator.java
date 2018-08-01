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
 * URIManipulator handles all content related to URIs
 */
public class URIManipulator
    extends AbstractCharacterManipulator
{
    // These characters are allowed in any URI Context, to be used in subclasses
    protected static final Set<Character> baseImmuneCharacters =
        Collections.unmodifiableSet( new HashSet<Character>( Arrays.asList( '-', '_', '.', '~' ) ) );

    private Set<Character> immuneCharacters;

    protected URIManipulator( Set<Character> immuneCharacters )
    {
        this.immuneCharacters = immuneCharacters;
    }

    @Override
    protected String getCorrectCharacter( Character c )
    {
        String correctedCharacter = "";

        // If the character is alphanumeric, or immune, it is OK
        if ( isAlphaNum( c ) || this.immuneCharacters.contains( c ) )
        {
            correctedCharacter = String.valueOf( c );
        }
        // Otherwise, Percent encode the hex representation
        else
        {
            correctedCharacter = "%" + getHexForCharacter( c );
        }

        return correctedCharacter;
    }
}
