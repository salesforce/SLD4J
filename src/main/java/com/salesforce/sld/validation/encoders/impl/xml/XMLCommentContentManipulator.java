/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.validation.encoders.impl.xml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.salesforce.sld.validation.encoders.impl.XMLManipulator;

public class XMLCommentContentManipulator
    extends XMLManipulator
{
    private static final Set<Character> immuneCharacters = Stream
        .concat( baseImmuneCharacters.stream(), new HashSet<Character>( Arrays.asList( '"', '\'', '<', '!', '>', '#',
            '$', '%', '^', '*', '+', '/', '=', '?', '@', '[', '\\', ']', '{', '|', '}', '~' ) ).stream() )
        .collect( Collectors.toSet() );

    public XMLCommentContentManipulator()
    {
        super( immuneCharacters );
    }

}
