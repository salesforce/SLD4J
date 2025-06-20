/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.networking;

import com.salesforce.sld.foundation.exception.SecurityControlRuntimeException;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author hdannouni
 * @author csmith
 *
 */
@RunWith(Parameterized.class)
public abstract class BaseAddressCheckerTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	CustomInternalAddressHandler handler = new CustomInternalAddressHandler();

	static class CustomInternalAddressHandler implements InternalAddressErrorHandler {

		String message;

		@Override
		public void handleValidationError(String host) {
			this.message = host;
		}

	};

	@Parameter(0)
	public String address;

	@Parameter(1)
	public Class<Exception> expectedException;

	@Parameters(name = "ip={0}, expectedException={1}")
	public static List<Object[]> addresses() {
		return Arrays.asList(new Object[][] {
				// @formatter:off
				// external IP space IPv4 valid values
                { "http://9.255.255.255", null },
                { "http://11.0.0.0", null },
                { "http://126.255.255.255", null },
                { "http://128.0.0.0", null },
                { "http://172.15.255.255", null },
                { "http://172.32.0.0", null },
                { "http://192.167.255.255", null },
                {  "http://192.169.0.0", null },
                {  "http://192.169.0.0/test", null },
                {  "http://192.169.0.0/test?var=value", null },
             
                // Testing url parsing logic
                { "http://127.0.0.1:8000#@example.com/", SecurityControlRuntimeException.class },
                { "http://127.0.0.1#@example.com/", SecurityControlRuntimeException.class },
                { "http://127.0.0.1:8000/@example.com/", SecurityControlRuntimeException.class },
                { "http://127.0.0.1/@example.com/", SecurityControlRuntimeException.class },
                { "http://127.0.0.1@example.com/#@example.com", null },
                { "http://127.0.0.1:8000@example.com/#@example.com", null },

                // IPv4 internal and restricted IP addresses
                { "http://0.0.0.0", SecurityControlRuntimeException.class },
                { "http://10.0.0.0", SecurityControlRuntimeException.class },
                { "http://10.255.255.255", SecurityControlRuntimeException.class },
                { "http://100.64.0.0", SecurityControlRuntimeException.class },
                { "http://100.127.255.255", SecurityControlRuntimeException.class },
                { "http://127.0.0.0", SecurityControlRuntimeException.class },
                { "http://127.255.255.255", SecurityControlRuntimeException.class },
                { "http://168.63.129.16", SecurityControlRuntimeException.class },
                { "http://169.254.0.0", SecurityControlRuntimeException.class },
                { "http://169.254.169.254", SecurityControlRuntimeException.class },
                { "http://169.254.255.255", SecurityControlRuntimeException.class },//
                { "http://172.16.0.0", SecurityControlRuntimeException.class },//
                { "http://172.31.255.255", SecurityControlRuntimeException.class },//
                { "http://192.0.0.0", SecurityControlRuntimeException.class },
                { "http://192.0.0.255", SecurityControlRuntimeException.class },//
                { "http://192.0.2.0", SecurityControlRuntimeException.class },//
                { "http://192.0.2.255", SecurityControlRuntimeException.class },//
                { "http://192.18.0.0", SecurityControlRuntimeException.class },
                { "http://192.19.255.255", SecurityControlRuntimeException.class },//
                { "http://192.168.0.0", SecurityControlRuntimeException.class },//
                { "http://192.168.255.255", SecurityControlRuntimeException.class },//
                { "http://198.51.100.0", SecurityControlRuntimeException.class },//
                { "http://198.51.100.255", SecurityControlRuntimeException.class },//
                { "http://203.0.113.0", SecurityControlRuntimeException.class },//
                { "http://203.0.113.255", SecurityControlRuntimeException.class },//
                { "http://224.0.0.0", SecurityControlRuntimeException.class },
                { "http://239.255.255.255", SecurityControlRuntimeException.class },//
                { "http://240.0.0.0", SecurityControlRuntimeException.class },
                { "http://255.255.255.255", SecurityControlRuntimeException.class },
                
                // IPv6 internal and restricted IP addresses
                { "http://[::]", SecurityControlRuntimeException.class },
                { "http://[::1]", SecurityControlRuntimeException.class },
                { "http://[100::]", SecurityControlRuntimeException.class },
                { "http://[100::FFFF:FFFF:FFFF:FFFF]", SecurityControlRuntimeException.class },
                { "http://[2001::]", SecurityControlRuntimeException.class },
                { "http://[2001::FFFF:FFFF:FFFF:FFFF:FFFF:FFFF]", SecurityControlRuntimeException.class },
                { "http://[2001:20::]", SecurityControlRuntimeException.class },
                { "http://[2001:2F:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF]", SecurityControlRuntimeException.class },
                { "http://[2001:DB8::]", SecurityControlRuntimeException.class },
                { "http://[2001:DB8:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF]", SecurityControlRuntimeException.class },
                { "http://[FC00::]", SecurityControlRuntimeException.class },
                { "http://[FDFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF]", SecurityControlRuntimeException.class },
                { "http://[FE80::]", SecurityControlRuntimeException.class },
                { "http://[FEBF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF]", SecurityControlRuntimeException.class },
                { "http://[FEC0::]", SecurityControlRuntimeException.class },
                { "http://[FEFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF]", SecurityControlRuntimeException.class },
                { "http://[FF00::]", SecurityControlRuntimeException.class },
                { "http://[FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF]", SecurityControlRuntimeException.class },

                // AWS metadata API URL
                { "http://169.254.169.254", SecurityControlRuntimeException.class },
                { "http://169.254.169.254/latest/meta-data/", SecurityControlRuntimeException.class },
                
                // Azure metadata and wireserver IP addresses
                { "http://169.254.169.254/metadata/instance", SecurityControlRuntimeException.class },
                { "http://168.63.129.16", SecurityControlRuntimeException.class },
                

                // testing unicode zero digits
                {"http://0:8080", SecurityControlRuntimeException.class },
//                {"http://0x0:8080", SecurityControlRuntimeException.class },
                {"http://00000000000:8080", SecurityControlRuntimeException.class },
                {"http://[::0]:8080", SecurityControlRuntimeException.class },
                {"http://[::]:8080", SecurityControlRuntimeException.class },
//                {"http://\u17E0:8080", SecurityControlRuntimeException.class },
//                {"http://\u0A66:8080", SecurityControlRuntimeException.class },
//                {"http://\uFF10:8080", SecurityControlRuntimeException.class },
                {"http://0.0.0.0", SecurityControlRuntimeException.class },
                {"http://[0:0:0:0:0:0:0:0]", SecurityControlRuntimeException.class },
                

                // internal IPv6 space local loopback
                { "http://[::ffff:0:0]", SecurityControlRuntimeException.class},
                { "http://[::1]", SecurityControlRuntimeException.class },
                { "http://[0:0:0:0:0:0:0:1]", SecurityControlRuntimeException.class },
                { "http://[0000:0000:0000:0000:0000:0000:0000:0001]", SecurityControlRuntimeException.class },

                // known internal hostname
                { "http://localhost/test?var=value", SecurityControlRuntimeException.class },

                // external address space unknown host
                { "https://this.host.should.not.exist.salesforce.net", UnknownHostException.class },
                { "https://this.host.should.not.exist.salesforce.net/test", UnknownHostException.class },
                { "https://this.host.should.not.exist.salesforce.net/test?var=value", UnknownHostException.class }
                //@formatter:on
		});
	}
}
