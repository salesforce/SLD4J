/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.encoders.impl.json;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.salesforce.sld.encoders.impl.JSONManipulator;

public class JSONValueManipulator
    extends JSONManipulator
{
    private static final Set<Character> escapeCharacters =
        new HashSet<Character>( Arrays.asList( '\b', '\t', '\n', '\f', '\r', '"', '\\', '/' ) );

    public JSONValueManipulator()
    {
        super( escapeCharacters );
    }

}
