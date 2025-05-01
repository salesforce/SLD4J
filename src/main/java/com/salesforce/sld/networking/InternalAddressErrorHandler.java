/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.networking;

/**
 * Handler interface to allow engineers to build implementations to determine
 * control flow when a validation error occurs.
 * 
 * @author csmith
 */
public interface InternalAddressErrorHandler
{
    /**
     * Called when the {@linkplain AddressCheckerInterface} detects a
     * host/url is an internal address
     * 
     * @param host the host that caused the checker to fail
     */
    public void handleValidationError( String host );
}
