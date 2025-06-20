/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.networking;

import com.salesforce.sld.foundation.exception.SecurityControlRuntimeException;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author hdannouni
 *
 */
public class CIDRBasedAddressCheckerUtilTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public static final String TEST_IP = "64.64.64.64";
	public static final String TEST_URL = "http://64.64.64.64";

	private CIDRBasedAddressChecker cidrChecker;

	@Before
	public void setCIDRChecker() {
		// Create a CIDRBasedAddressChecker with custom deny-list
		IpSubnetFilterRule rule = new IpSubnetFilterRule("127.0.0.0", 8, IpFilterRuleType.REJECT);
		List<IpSubnetFilterRule> ruleList = new ArrayList<IpSubnetFilterRule>();
		ruleList.add(rule);
		cidrChecker = new CIDRBasedAddressChecker(ruleList, new DefaultInternalAddressErrorHandler());
	}

	@Test
	public void testCustomCIDRAddressChecker()
			throws SecurityControlRuntimeException, MalformedURLException, UnknownHostException {

		// Test the CIDRBasedAddressChecker blocks IPs from the deny-list only
		cidrChecker.checkUrl(TEST_URL);
		exception.expect(SecurityControlRuntimeException.class);
		cidrChecker.checkUrl("http://127.0.0.0");

	}

	@Test
	public void testDenyFilter() throws UnknownHostException, SecurityControlRuntimeException, MalformedURLException {
		// Create a denyFilter to block TEST_IP
		InetAddressFilter denyFilter = new InetAddressFilter() {

			InetAddress denyAddress = InetAddress.getAllByName(TEST_IP)[0];

			@Override
			public boolean test(InetAddress t) {
				return denyAddress.equals(t);
			}
		};
		// Test the deny-filter blocks the TEST_IP
		cidrChecker.setDenyFilter(denyFilter);
		exception.expect(SecurityControlRuntimeException.class);
		cidrChecker.checkUrl(TEST_URL);
	}

	@Test
	public void testAllowFilter() throws UnknownHostException, SecurityControlRuntimeException, MalformedURLException {
		// Create an allow-filter to allow the IP 127.0.0.0
		InetAddressFilter allowFilter = new InetAddressFilter() {

			InetAddress denyAddress = InetAddress.getAllByName("127.0.0.0")[0];

			@Override
			public boolean test(InetAddress t) {
				return denyAddress.equals(t);
			}
		};
		cidrChecker.setAllowFilter(allowFilter);
		// Test the allow-filter allows the TEST_IP
		exception = ExpectedException.none();
		cidrChecker.checkUrl("http://127.0.0.0");
	}

	@Test
	public void testGetterSetter() {
		InetAddressFilter denyFilter = new InetAddressFilter() {

			@Override
			public boolean test(InetAddress t) {
				return true;
			}
		};
		InetAddressFilter allowFilter = new InetAddressFilter() {

			@Override
			public boolean test(InetAddress t) {
				return false;
			}
		};
		cidrChecker.setDenyFilter(denyFilter);
		cidrChecker.setAllowFilter(allowFilter);
		// Test setters and getter
		assertEquals(denyFilter, cidrChecker.getDenyFilter());
		assertEquals(allowFilter, cidrChecker.getAllowFilter());
	}

}
