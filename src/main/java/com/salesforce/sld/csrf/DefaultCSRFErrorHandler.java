/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.csrf;

/**
 * A default implementation of {@linkplain ICSRFErrorHandler} that simply dumps the passed data to the system error log
 * 
 * @author Chris Smith
 */
public class DefaultCSRFErrorHandler
    implements ICSRFErrorHandler
{

    public void handleValidationError( String message )
    {
        System.err.println( message );
    }

    public void handleInternalError( String message )
    {
        System.err.println( message );
    }

    public void handleFatalException( String message, Exception e )
    {
        throw new SecurityException( message, e );
    }

}
