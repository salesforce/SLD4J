/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.networking;

import com.salesforce.sld.foundation.exception.SecurityControlRuntimeException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * 
 * @author hdannouni
 *
 */
public interface AddressCheckerInterface {

	/**
	 * This method resolves the given url to a InetAddress and checks if the
	 * IP address is an internal or restricted address.
	 * <p>
	 * The purpose of this method is to help prevent SSRF attacks
	 * 
	 * @param origUrl to check
	 * @throws MalformedURLException when the URL is invalid
	 * @throws UnknownHostException when the host name could not be resolved
	 * @throws SecurityControlRuntimeException when the address is an internal
	 *             IP and the default {@linkplain InternalAddressErrorHandler}
	 *             is used
	 */
	default void checkUrl(String origUrl) throws MalformedURLException, UnknownHostException, SecurityControlRuntimeException{
		URL url = new URL( origUrl );
		String host = url.getHost();
        checkHost( host );
	}

	/**
	 * This method resolves the given host to a InetAddress and checks if the
	 * address is an internal or restricted IP.
	 * <p>
	 * The purpose of this method is to help prevent SSRF attacks
	 * 
	 * @param host to check
	 * @throws UnknownHostException when the host name could not be resolved
	 * @throws SecurityControlRuntimeException when the address is an internal
	 *             IP and the default {@linkplain InternalAddressErrorHandler}
	 *             is used
	 */
	void checkHost(String host) throws UnknownHostException, SecurityControlRuntimeException;

}