/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.networking;

import com.salesforce.sld.foundation.exception.SecurityControlRuntimeException;
import org.junit.Test;

import java.net.*;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author hdannouni
 *
 */
public class CIDRBasedAddressCheckerTest extends BaseAddressCheckerTest {

	@Test
	public void testInternalAddressChecker()
			throws ConnectException, UnknownHostException, MalformedURLException, SecurityControlRuntimeException {
		CIDRBasedAddressChecker checker = new CIDRBasedAddressChecker();
		if (expectedException != null) {
			exception.expect(expectedException);
		}

		checker.checkUrl(address);
	}

	@Test
	public void testInternalAddressCheckerHandler()
			throws SecurityControlRuntimeException, MalformedURLException, UnknownHostException {

		boolean checkmsg = false;
		if (expectedException != null) {
			if (expectedException.equals(SecurityControlRuntimeException.class)) {
				checkmsg = true;
			} else {
				exception.expect(expectedException);
			}
		}

		new CIDRBasedAddressChecker(handler).checkUrl(address);

		if (checkmsg) {
			String expected = URI.create(address).getHost();
			if (expected == null) {
				// This is an opaque URI, but a URL works.
				expected = new URL(address).getHost();
			}
			assertEquals(expected, handler.message);
		}
	}	
	
}
