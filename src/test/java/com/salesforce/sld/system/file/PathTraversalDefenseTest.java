/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.system.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PathTraversalDefenseTest {

	private SubPathValidator pathComparator = new SubPathValidator();

	@Parameters(name = "f1={0}, f2={1}, expect={2}")
	public static List<Object[]> params() {
		return Arrays.asList(new Object[][] {
			{ "/foo/bar", "/foo/bar/baz", true }, // case where testPath is a valid sub path of basePath
			{ "/foo/bar", "/foo", false }, // case where testPath is a root of basePath. Test should fail
			{ "/foo/bar", "/foo/bar/../", false }, // case where normalized testPath is a root of basePath. Test should fail
			{ "/foo/bar", "/foo/bar/../../", false }, // case where normalized testPath is a root of basePath. Test should fail
			{ "/foo/bar", "/foo/bar/../bar/baz", true }, // case where normalized testPath is a valid sub path of basePath
			{ "foo/bar/", "/test/dir/", false }, // case where basePath and testPath share the same root, but are different. Test should fail
			{ "foo/bar/", "../../dir/", false }, // case where testing relative paths. Test should fail
			{ "foo/bar/", "../foo/bar/", false }, // case where testPath and basePath have similar path with different roots. Test should fail
			{ "foo/bar/test.txt", "foo/bar/", false }, // case where testPath is a root of basePath. Test should fail
			{ "foo/bar", "foo/bar/test.txt", true }, // case where testPath is a valid sub path of basePath
			{ "test/dir", "test/dir/./foo", true }, }); // case where normalized testPath is a valid sub path of basePath
	}

	@Parameter(0)
	public String basePath;

	@Parameter(1)
	public String testPath;

	@Parameter(2)
	public boolean expected;

	@Test
	public void testParameterizedAsString() {
		boolean isSubPath = pathComparator.isSubPath( basePath, testPath);
		assertEquals(isSubPath, expected);
	}

	@Test
	public void testParameterizedAsFile() {
		File baseFile = new File(basePath);
		File testFile = new File(testPath);
		boolean isSubPath = pathComparator.isSubPath( baseFile, testFile);
		assertEquals(isSubPath, expected);
	}

	@Test
	public void testParameterizedAsPath() {
		
		boolean isSubPath = pathComparator.isSubPath(Paths.get( basePath), Paths.get( testPath));
		assertEquals(isSubPath, expected);
	}

}
