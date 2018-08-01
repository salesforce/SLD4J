/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.encoders.impl.uri;

import java.util.Set;

import com.salesforce.sld.encoders.impl.URIManipulator;

public class URIStrictManipulator
    extends URIManipulator
{
    private static final Set<Character> immuneCharacters = baseImmuneCharacters;

    public URIStrictManipulator()
    {
        super( immuneCharacters );
    }

}
