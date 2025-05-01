/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.foundation.exception;

/**
 * Base Exception for all security problems encountered during processing from any applicable control in this library
 * 
 * @author csmith
 */
public class SecurityControlException
    extends Exception
{
    private static final long serialVersionUID = 4371162032061627362L;

    public SecurityControlException( String string )
    {
        super( string );
    }
}
