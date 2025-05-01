/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.networking;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 
 * @author hdannouni
 *
 */
public class DefaultIpAdressFilterTest {

	@Test
	public void testIDefaultIpAdressFilter() {
		DefaultIpAddressFilter filter = new DefaultIpAddressFilter();
		InetAddress address = null;
		try {
			address = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		assertFalse(filter.test(address));
		filter.addIpAddress(address);
		assertTrue(filter.test(address));
		filter.removeIpAddress(address);
		assertFalse(filter.test(address));
	}

}
