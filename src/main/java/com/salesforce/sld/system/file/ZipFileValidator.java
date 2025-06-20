/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.system.file;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.salesforce.sld.foundation.exception.SecurityControlException;

/**
 * This validator ensures that a zip file extracts within it's root directory
 * 
 * @author csmith
 * @author hdannouni
 *
 */
public class ZipFileValidator {

	private SubPathValidator pathComparator = new SubPathValidator();

	/**
	 * Ensure that all zipped files don't unzip to a path above the output
	 * directory.
	 * <p>
	 * <b>This does not actually place the files on the filesystem.</b>
	 * <p>
	 * This defends against Path Traversal vulnerabilities
	 * 
	 * @param zipFile
	 *            the Zip file
	 * @throws SecurityControlException
	 *             if a file would have been unzipped to a location above the
	 *             outputDirectory
	 * @throws IOException
	 *             if an IO error occurs while reading the zip
	 */
	public void validateUnzipPath(ZipFile zipFile) throws SecurityControlException, IOException {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		File outputDirectory = new File("tmpOutput/");

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();

			String entryName = entry.getName();

			File entryDestination = new File(outputDirectory, entryName);

			if (!pathComparator.isSubPath(outputDirectory, entryDestination)) {
				throw new SecurityControlException("Zip entry " + entryName + " in zip file " + zipFile.getName()
						+ " is located outside of the zip file output directory ");
			}
		}
	}

	/**
	 * Ensure that all zipped files don't unzip to a path above the output
	 * directory.
	 * <p>
	 * <b>This does not actually place the files on the filesystem.</b>
	 * <p>
	 * This defends against Path Traversal vulnerabilities
	 * 
	 * @param sourceFile
	 *            the Zip file
	 * @throws SecurityControlException
	 *             if a file would have been unzipped to a location above the
	 *             outputDirectory
	 * @throws IOException
	 *             if an IO error occurs while reading the zip
	 */
	public void validateUnzipPath(File sourceFile) throws SecurityControlException, IOException {
		try (ZipFile zipFile = new ZipFile(sourceFile)) {
			validateUnzipPath(zipFile);
		}
	}
}
