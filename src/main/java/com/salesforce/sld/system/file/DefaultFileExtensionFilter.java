/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.system.file;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * This class implements an {@linkplain IFileExtensionFilter} based on a list of
 * extensions
 * <p>
 * {@linkplain DefaultFileExtensionFilter} supports adding
 * and removing extensions to the current filter
 * <p>
 * {@linkplain DefaultFileExtensionFilter} could be used as allowFilter in
 * {@linkplain FileNameValidator}
 *
 * @author hdannouni
 *
 */
public class DefaultFileExtensionFilter implements IFileExtensionFilter {
    private final List<String> filteredExtensions = new ArrayList<String>();

    @Override
    public boolean testExtension(String extension) {
        for (String allowed : filteredExtensions) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addExtension(@Nonnull String extension) {
        if (extension != null) {
            filteredExtensions.add(extension);
        }
    }

    /**
     * Removes an extension from the filter list
     *
     * @param extension the string to be removed from the filteredExtensions
     */
    public void removeExtension(@Nonnull String extension) {
        if (extension != null) {
            filteredExtensions.remove(extension);
        }
    }

}
