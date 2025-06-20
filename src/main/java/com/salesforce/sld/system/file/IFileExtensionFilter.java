/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.system.file;

import javax.annotation.Nonnull;

/**
 * Interface to be implemented for allowedExtensions at {@linkplain FileNameValidator}
 *
 * @author msacchetin
 *
 */
public interface IFileExtensionFilter{
    /**
     * Test whether an extension is allowed.
     *
     * @param extension the string to be compared to the extension filter
     * @return true if the extension is allowed
     */
    boolean testExtension(String extension);

    /**
     * Adds an extension to the filter
     *
     * @param extension the string to be added to the extension filter
     */
    void addExtension(@Nonnull String extension);
}
