/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.networking;

import com.salesforce.sld.foundation.exception.SecurityControlRuntimeException;

/**
 * Default implementation of the {@linkplain InternalAddressErrorHandler} that
 * throws an unchecked exception
 * 
 * @author csmith
 */
public class DefaultInternalAddressErrorHandler
    implements InternalAddressErrorHandler
{

    /**
     * By default, throw a {@linkplain SecurityControlRuntimeException} with an
     * error message about the failure
     * 
     * @param host the host that caused the checker to fail
     * @throws SecurityControlRuntimeException in all cases
     */
    @Override
    public void handleValidationError( String host )
        throws SecurityControlRuntimeException
    {
        throw new SecurityControlRuntimeException( "Access attempt to internal IP address space: " + host );
    }

}
