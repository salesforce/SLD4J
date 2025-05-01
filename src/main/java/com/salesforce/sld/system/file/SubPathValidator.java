/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.system.file;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple methods to "compare" paths to help defend against Path traversal
 * see https://www.owasp.org/index.php/Path_Traversal
 * 
 * @author csmith
 * @author hdannouni
 */
public class SubPathValidator {


	/**
	 * Test whether the provided testPath is a normalized subpath of the
	 * provided basepath.
	 * Path comparison is case sensitive
	 * <p>
	 * This defends against Path Traversal vulnerabilities
	 * 
	 * @param intendedBasePath
	 *            a String denoting a path to a resource
	 * @param testPath
	 *            a String denoting a path to a resource
	 * @return true if the intendedBasePath is a subpath of the testPath
	 */
	public boolean isSubPath(String intendedBasePath, String testPath) {
		return isSubPath(Paths.get(intendedBasePath), Paths.get(testPath));
	}

	/**
	 * Test whether the provided testPath is a normalized subpath of the
	 * provided basepath.
	 * Path comparison is case sensitive
	 * <p>
	 * This defends against Path Traversal vulnerabilities
	 * 
	 * @param intendedBasePath
	 *            a File describing to a resource
	 * @param testPath
	 *            a File describing to a resource
	 * @return true if the intendedBasePath is a subpath of the testPath
	 */
	public boolean isSubPath(File intendedBasePath, File testPath) {
		return isSubPath(intendedBasePath.toPath(), testPath.toPath());
	}

	/**
	 * Test whether the provided testPath is a normalized subpath of the
	 * provided basepath.
	 * Path comparison is case sensitive
	 * <p>
	 * This defends against Path Traversal vulnerabilities
	 * 
	 * @param intendedBasePath
	 *            a Path to a resource
	 * @param testPath
	 *            a Path to a resource
	 * @return true if the intendedBasePath is a subpath of the testPath
	 */
	public boolean isSubPath(Path intendedBasePath, Path testPath) {
		Path normalizedBase = intendedBasePath.normalize();
		Path normalizedTest = testPath.normalize();

		return normalizedTest.startsWith(normalizedBase);
	}
}
