/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.encoders.impl.js;

import java.util.Set;

import com.salesforce.sld.encoders.impl.JavaScriptManipulator;

public class JavaScriptAttrManipulator
    extends JavaScriptManipulator
{

    private static final Set<Character> escapeCharacters = baseEscapeList;

    private static final Set<Character> ignoreCharacters = baseIgnoreList;

    public JavaScriptAttrManipulator()
    {
        super( ignoreCharacters, escapeCharacters );
    }

}
