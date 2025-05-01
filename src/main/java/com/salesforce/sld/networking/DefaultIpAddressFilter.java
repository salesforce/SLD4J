/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.networking;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements an {@linkplain InetAddressFilter} based on a list of
 * {@linkplain InetAddress}
 * <p>
 * {@linkplain DefaultIpAddressFilter} supports adding
 * and removing IP addresses presented by {@linkplain InetAddress}
 * <p>
 * {@linkplain DefaultIpAddressFilter} could be used as allowFilter or denFilter in
 * {@linkplain CIDRBasedAddressChecker}
 * 
 * @author hdannouni
 *
 */
public class DefaultIpAddressFilter implements InetAddressFilter {

	private final List<InetAddress> filteredAddresses = new ArrayList<InetAddress>();

	@Override
	public boolean test(InetAddress inetAddress) {
		for (InetAddress allowed : filteredAddresses) {
			if (allowed.equals(inetAddress)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds an address to the filter list
	 * 
	 * @param address the InetAddress to be added to the filteredAddresses
	 */
	public void addIpAddress(@Nonnull InetAddress address) {
		if (address != null) {
			filteredAddresses.add(address);
		}
	}

	/**
	 * Removes an address from the filter list
	 * 
	 * @param address the InetAddress to be removed from the filteredAddresses
	 */
	public void removeIpAddress(@Nonnull InetAddress address) {
		if (address != null) {
			filteredAddresses.remove(address);
		}
	}

}
