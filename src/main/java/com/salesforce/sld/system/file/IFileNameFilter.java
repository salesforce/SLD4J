/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.system.file;

import java.util.function.Predicate;

/**
 * Interface implemented for allowedName in {@linkplain FileNameValidator}
 *
 * @author sarah.lackey
 */
public interface IFileNameFilter
                extends Predicate<String>

{


}
