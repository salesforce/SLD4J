/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.foundation.exception;

/**
 * Base Unchecked Exception for all security problems encountered during processing from any applicable control in this library
 * 
 * @author csmith
 */
public class SecurityControlRuntimeException
    extends SecurityException
{
    private static final long serialVersionUID = 4371162032061627362L;

    public SecurityControlRuntimeException( String string )
    {
        super( string );
    }
    
    public SecurityControlRuntimeException( String string, Exception ex )
    {
        super( string, ex );
    }
}
