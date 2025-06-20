/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.validation.csrf;

/**
 * A Handler to be used in conjunction with the {@linkplain StatelessCSRFTokenManager}. The handler allows special configuration
 * to handle error/exception cases. It is recommended that implementors create a {@linkplain ICSRFErrorHandler} that
 * utilizes their logging mechanisms for their application
 * 
 * @author Chris Smith
 */
public interface ICSRFErrorHandler
{
    /**
     * Called when a CSRF Token cannot be validated for some reason
     * 
     * @param message the reason the token is invalid
     */
    public void handleValidationError( String message );

    /**
     * Called when input to a CSRF function does not meet the required criteria for that function
     * 
     * @param message the reason this error was thrown
     */
    public void handleInternalError( String message );

    /**
     * Called when a function encounters an exception it cannot recover from
     * 
     * @param message the current state of the function when the exception was thrown
     * @param e the exception thrown
     */
    public void handleFatalException( String message, Exception e );
}
