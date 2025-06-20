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
 * @author sarah.lackey
 *
 */
public class DefaultFileNameFilterTest {

    @Test
    public void testDefaultFileNameFilterTest() {
        DefaultFileNameFilter filter = new DefaultFileNameFilter();
        String pattern = "[a-zA-Z()_.,-]*";
        assertFalse(filter.test("abcXYZabc-,.,)(-_"));
        filter.addPattern(pattern);
        assertTrue(filter.test("abcXYZabc-,.,)(-_"));
        assertTrue(filter.test("bar"));
        filter.removeAllPatterns();
        assertFalse(filter.test("abcXYZabc-,.,)(-_"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testPatternException() {
        DefaultFileNameFilter filter = new DefaultFileNameFilter();
        String pattern = "*****Sdefwe**";
        filter.addPattern( pattern );
    }
}
