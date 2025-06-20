/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.networking;

import java.net.InetAddress;
import java.util.function.Predicate;

/**
 * Interface to be implemented for allowFilter or denyFilter at
 * {@linkplain CIDRBasedAddressChecker}
 * 
 * @author hdannouni
 *
 */
public interface InetAddressFilter extends Predicate<InetAddress> {

}
