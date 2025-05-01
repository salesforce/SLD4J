/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.networking;

import com.salesforce.sld.foundation.exception.SecurityControlRuntimeException;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class checks a URL against a list of known internal or reserved subnets
 * to protect from server side request forgery attacks. The default list of
 * denied subnets is configured in the DENY_LIST_YAML file.
 * <p>
 * The address checker should be used to verify the url/host before making any
 * calls to that url/host.
 * <p>
 * NB: If an http request returns a status code 3xx redirection, the new
 * location should be checked before following the redirection
 * 
 * @author hdannouni
 *
 */
public class CIDRBasedAddressChecker implements AddressCheckerInterface {

	// default deny list config file
	public static final String DENY_LIST_YAML = "denylist.yml";

	// This port is used to instanciate InetSocketAddress. the port is not used to
	// allow/deny a url. Only the resolved IP address is used
	private static final int DEFAULT_PORT = 49152;

	private final List<IpSubnetFilterRule> subnets = new ArrayList<IpSubnetFilterRule>(36);
	// Error handler
	private final InternalAddressErrorHandler handler;

	// This filter allows a set of IP addresses
	private InetAddressFilter allowFilter = new DefaultIpAddressFilter();

	// This filter denies a set of IP addresses
	private InetAddressFilter denyFilter = new DefaultIpAddressFilter();

	/**
	 * Default constructor. This constructor uses the default DENY_LIST_FILE file,
	 * {@linkplain InternalAddressErrorHandler} and two instances of
	 * {@linkplain DefaultIpAddressFilter} as allowFilter and denyFilter
	 */
	public CIDRBasedAddressChecker() {
		this(null);
	}

	/**
	 * Create a new {@linkplain CIDRBasedAddressChecker} with the provided
	 * {@linkplain InternalAddressErrorHandler}
	 * 
	 * @param handler: if the handler is null, a
	 *                 {@linkplain DefaultInternalAddressErrorHandler} is used
	 */
	public CIDRBasedAddressChecker(InternalAddressErrorHandler handler) {
		if (handler == null) {
			handler = new DefaultInternalAddressErrorHandler();
		}
		this.handler = handler;
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DENY_LIST_YAML);
			Yaml yaml = new Yaml();
			List<String> denyList = yaml.load(inputStream);
			denyList.forEach(c -> addCIDR(c));
		} catch (RuntimeException e) {
			throw new SecurityControlRuntimeException("Could not add all deny-listed subnets", e);
		}
	}

	/**
	 * This constructor creates a {@linkplain CIDRBasedAddressChecker} with the
	 * customDenyList. The security of this instance of
	 * {@linkplain CIDRBasedAddressChecker} solely relies on the completeness of the
	 * customDenyList
	 * <p>
	 * Review the default deny-list for a list of subnets that should potentially be
	 * denied
	 * 
	 * @param customDenyList The list of subnets to be denied
	 * @param handler        error handler to manage denied IP addresses
	 */
	public CIDRBasedAddressChecker(List<IpSubnetFilterRule> customDenyList, InternalAddressErrorHandler handler) {
		super();
		this.handler = handler;
		customDenyList.forEach(rule -> subnets.add(rule));
	}

	/**
	 * Method used to populate the default denylist
	 * 
	 * @param cidr: a CIDR representation of a subnet
	 */
	private void addCIDR(String cidr) {
		String[] tokens = cidr.split("/");

		if (tokens.length == 2) {
			int mask = Integer.parseInt(tokens[1]);
			IpSubnetFilterRule rule = new IpSubnetFilterRule(tokens[0], mask, IpFilterRuleType.REJECT);
			subnets.add(rule);
		} else {
			throw new IllegalArgumentException("Invalid CIDR argument: " + cidr);
		}
	}

	@Override
	public void checkHost(String host) throws UnknownHostException, SecurityControlRuntimeException {
		// Resolving the IP address of the hostname
		InetAddress inetAddress;

		inetAddress = InetAddress.getByName(host);
		if (allowFilter.test(inetAddress)) {// the IP address is allowed by the allowFiter
			return;
		}
		if (isRestricted(inetAddress) || denyFilter.test(inetAddress)) {
			handler.handleValidationError(host);
		}
		return;
	}

	/**
	 * checks the {@linkplain InetAddress} against the denylist
	 * 
	 * @param inetAddress
	 * @return true if the {@linkplain InetAddress} is deny-listed
	 */
	private boolean isRestricted(InetAddress inetAddress) {
		InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, DEFAULT_PORT);
		for (IpSubnetFilterRule subnet : subnets) {
			if (subnet.matches(inetSocketAddress)) {
				return true;
			}
		}
		return false;
	}

	public InetAddressFilter getAllowFilter() {
		return allowFilter;
	}

	public InetAddressFilter getDenyFilter() {
		return denyFilter;
	}

	public void setDenyFilter(InetAddressFilter denyFilter) {
		this.denyFilter = denyFilter;
	}

	public void setAllowFilter(InetAddressFilter allowFilter) {
		this.allowFilter = allowFilter;
	}

}
