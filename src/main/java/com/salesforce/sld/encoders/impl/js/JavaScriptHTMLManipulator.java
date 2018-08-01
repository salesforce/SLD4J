/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.encoders.impl.js;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.salesforce.sld.encoders.impl.JavaScriptManipulator;

public class JavaScriptHTMLManipulator
    extends JavaScriptManipulator
{
    private static final Set<Character> escapeCharacters =
        Stream.concat( baseEscapeList.stream(), new HashSet<Character>( Arrays.asList( '-', '/' ) ).stream() )
            .collect( Collectors.toSet() );

    private static final Set<Character> ignoreCharacters = baseIgnoreList;

    public JavaScriptHTMLManipulator()
    {
        super( ignoreCharacters, escapeCharacters );
    }

}
