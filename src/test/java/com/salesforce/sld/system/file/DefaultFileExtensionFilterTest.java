/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.system.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author msacchetin
 *
 */
public class DefaultFileExtensionFilterTest {

    @Test
    public void testDefaultFileExtensionFilterTest() {
        DefaultFileExtensionFilter filter = new DefaultFileExtensionFilter();
        String extension = ".jpg";
        assertFalse(filter.testExtension(extension));
        filter.addExtension(extension);
        assertTrue(filter.testExtension(extension));
        filter.removeExtension(extension);
        assertFalse(filter.testExtension(extension));
    }

}
