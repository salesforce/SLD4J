/*
 * Copyright (c) 2021, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.system.file;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple methods to help implementing file extension filters and checks
 * to help defend against malicious inputs such as double extensions.
 * see https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html#extension-validation
 *
 * @author msacchetin
 * @author hdannouni
 */
public class FileNameValidator {


    private IFileExtensionFilter allowedExtensions = null;
    private IFileNameFilter allowedFileRegexes = null;
    private int maxLength;
    /**
     * Construct a FileNameValidator instance.
     * <p>
     * It uses an implementation of {@linkplain IFileExtensionFilter} to
     * enforce the extension filter.
     *
     * @param extensionFilter
     *            a {@linkplain IFileExtensionFilter} that implements
     *            the extension filter
     * @param nameFilter
     *            a {@linkplain IFileNameFilter} that implements the
     *            name filter
     */
    public FileNameValidator(IFileExtensionFilter extensionFilter, IFileNameFilter nameFilter, int maxLength) {
        this.allowedExtensions = extensionFilter;
        this.allowedFileRegexes = nameFilter;
        this.maxLength = maxLength;
    }


    /**
     * Get file extension.
     * <p>
     * Get the file extension without the '.' char.
     *
     * @param testPath
     *            a String denoting a path to a resource
     * @return extension
     */
    public String getExtension(String testPath) {
        File f =  new File(testPath);
        return getExtension(f);
    }

    /**
     * Get file extension.
     * <p>
     * Get the file extension without the '.' char.
     *
     * @param testPath
     *            a File describing to a resource
     * @return extension
     */
    public String getExtension(File testPath) {
        return getExtension(testPath.toPath());
    }

    /**
     * Get file extension.
     * <p>
     * Get the file extension without the '.' char.
     *
     * @param testPath
     *            a Path to a resource
     * @return extension
     */
    public String getExtension(Path testPath) {
        Path fileName = testPath.getFileName();

        int extIndex = fileName.toString().indexOf('.');
        if (extIndex > 0) {
            return fileName.toString().substring(extIndex);
        }
        return "";
    }

    /**
     * Test whether the provided testPath refers to filename only with
     * no path.
     * <p>
     * This defends against malicious paths to evade valid filename checks
     *
     * @param testPath
     *            a String denoting a path to a resource
     * @return true if the testPath is only filename and not path
     */
    public boolean isFileNameOnlyPath(String testPath) {
        File f =  new File(testPath);
        return isFileNameOnlyPath(f);
    }

    /**
     * Test whether the provided testPath refers to filename only with
     * no path.
     * <p>
     * This defends against malicious paths to evade valid filename checks
     *
     * @param testPath
     *            a File describing to a resource
     * @return true if the testPath is only filename and not path
     */
    public boolean isFileNameOnlyPath(File testPath) {
        return isFileNameOnlyPath(testPath.toPath());
    }

    /**
     * Test whether the provided testPath refers to filename only with
     * no path.
     * <p>
     * This defends against malicious paths to evade valid filename checks
     *
     * @param testPath
     *            a Path to a resource
     * @return true if the testPath is only filename and not path
     */
    public boolean isFileNameOnlyPath(Path testPath) {
        Path fileName = testPath.getFileName();

        String fileNameOnlyPath = fileName.toString();
        String fileFullPath = testPath.toString();

        return fileFullPath.equalsIgnoreCase(fileNameOnlyPath);
    }

    /**
     * Test whether the provided testPath has single extension
     * for the file name.
     * <p>
     * This defends against malicious double extension file path inputs
     *
     * @param testPath
     *            a String denoting a path to a resource
     * @return true if the testPath is using single extension
     */
    public boolean isSingleExtension(String testPath) {
        File f =  new File(testPath);
        return isSingleExtension(f);
    }

    /**
     * Test whether the provided testPath has single extension
     * for the file name.
     * <p>
     * This defends against malicious double extension file path inputs
     *
     * @param testPath
     *            a File describing to a resource
     * @return true if the testPath is using single extension
     */
    public boolean isSingleExtension(File testPath) {
        return isSingleExtension(testPath.toPath());
    }

    /**
     * Test whether the provided testPath has single extension
     * for the file name.
     * <p>
     * This defends against malicious double extension file path inputs
     *
     * @param testPath
     *            a Path to a resource
     * @return true if the testPath is using single extension
     */
    public boolean isSingleExtension(Path testPath) {
        return !isDoubleExtension(testPath);
    }

    /**
     * Test whether the provided testPath has double extension
     * for the file name.
     * <p>
     * This defends against malicious double extension file path inputs
     *
     * @param testPath
     *            a String denoting a path to a resource
     * @return true if the testPath is using double extension
     */
    public boolean isDoubleExtension(String testPath) {
        return isDoubleExtension(Paths.get(testPath));
    }

    /**
     * Test whether the provided testPath has double extension
     * for the file name.
     * <p>
     * This defends against malicious double extension file path inputs
     *
     * @param testPath
     *            a Path to a resource
     * @return true if the testPath is using double extension
     */
    public boolean isDoubleExtension(Path testPath) {
        String Extension = getExtension(testPath);

        if(Extension.length() > 0)
        {
            int extIndexFirst = Extension.indexOf('.');
            int extIndexLast = Extension.lastIndexOf('.');
            return (extIndexFirst != extIndexLast);
        }
        return false;
    }

    /**
     * Test whether the provided testPath has valid extension according
     * to the filter.
     * <p>
     * This allows to implement whitelisting for only allowed files.
     *
     * @param testPath
     *            a String denoting a path to a resource
     * @return true if the testPath is using allowed extension
     */
    public boolean isAllowedExtension(String testPath) {
        return isAllowedExtension( Paths.get(testPath) );
    }

    /**
     * Test whether the provided testPath has valid extension according
     * to the filter.
     * <p>
     * This allows implementing whitelisting for only allowed files.
     *
     * @param testPath
     *            a String denoting a path to a resource
     * @param allowMultipleExtensions
     *            allow the last extension of a double/multiple extension path
     *            to be matched against the filter
     * @return true if the testPath is using allowed extension
     */
    public boolean isAllowedExtension(String testPath, boolean allowMultipleExtensions) {
        File f =  new File(testPath);
        return isAllowedExtension(f, allowMultipleExtensions);
    }

    /**
     * Test whether the provided testPath has valid extension according
     * to the filter.
     * <p>
     * This allows to implement whitelisting for only allowed files.
     *
     * @param testPath
     *            a File describing to a resource
     * @param allowMultipleExtensions
     *            allow the last extension of a double/multiple extension path
     *            to be matched against the filter
     * @return true if the testPath is using allowed extension
     */
    public boolean isAllowedExtension(File testPath, boolean allowMultipleExtensions) {
        return isAllowedExtension(testPath.toPath(), allowMultipleExtensions);
    }

    /**
     * Test whether the provided testPath has valid extension according
     * to the filter.
     * <p>
     * This allows to implement whitelisting for only allowed files.
     *
     * @param testPath
     *            a Path to a resource
     * @return true if the testPath is using allowed extension
     */
    public boolean isAllowedExtension(Path testPath) {
        return isAllowedExtension(testPath, false);
    }

    /**
     * Test whether the provided testPath has valid extension according
     * to the filter.
     * <p>
     * This allows to implement whitelisting for only allowed files.
     *
     * @param testPath
     *            a Path to a resource
     * @param allowMultipleExtensions
     *            allow the last extension of a double/multiple extension path
     *            to be matched against the filter
     * @return true if the testPath is using allowed extension
     */
    public boolean isAllowedExtension(Path testPath, boolean allowMultipleExtensions ) {
        String Extension = getExtension(testPath);
        boolean isDoubleExtension = isDoubleExtension(testPath);
        String lastExtension = Extension;

        if(isDoubleExtension)
        {
            // Retrieve the last extension from a multiple/double extension path
            int extIndex = testPath.toString().lastIndexOf('.');
            lastExtension = testPath.toString().substring(extIndex);
        }

        if(allowMultipleExtensions)
        {
            return allowedExtensions.testExtension("*") || allowedExtensions.testExtension(Extension) || allowedExtensions.testExtension(lastExtension);
        }

        return allowedExtensions.testExtension("*") || allowedExtensions.testExtension(Extension);
    }

    /**
     * Returns true if the given file name is a valid and allowed file name according to
     * the allowed file regexes defined with the filter.
     *
     * @param testPath
     *              the String path to the file to test
     * @param checkExtension
     *              true if the extension must also be checked with the regex
     * @return true if the file name is valid
     */
    public boolean isAllowedFileName(String testPath, boolean checkExtension) {
        File f =  new File(testPath);
        return isAllowedFileName(f, checkExtension);
    }

    /**
     * Returns true if the given file name is a valid and allowed file name according to
     * the allowed file regexes defined with the filter.
     *
     * @param testPath
     *              the String path to the file to test
     * @return true if the file name is valid
     */
    public boolean isAllowedFileName(String testPath) {
        return isAllowedFileName(testPath, false);
    }

    /**
     * Returns true if the given file name is a valid and allowed file name according to
     * the allowed file regexes defined with the filter.
     *
     * @param testPath
     *              the File path to the file to test
     * @param checkExtension
     *              true if the extension of the file must also be checked with the regex
     * @return true if the file name is valid
     */
    public boolean isAllowedFileName(File testPath, boolean checkExtension) {
        return isAllowedFileName(testPath.toPath(), checkExtension);
    }

    /**
     * Returns true if the given file name is a valid and allowed file name according to
     * the allowed file regexes defined with the filter.
     * @param testPath
     *              the Path to the file to test
     * @param checkExtension
     *              true if the extension must also be checked with the regex
     * @return true if the file name is valid
     */
    public boolean isAllowedFileName(Path testPath, boolean checkExtension) {
        String nameString = testPath.getFileName().toString();

        if (!checkExtension && isAllowedExtension(testPath)) {
            int extIndex = testPath.toString().indexOf('.');

            if (extIndex > -1) {
                nameString = testPath.getFileName().toString().substring(0, extIndex);

            }
        }
        return nameString.length() <= maxLength && allowedFileRegexes.test(nameString);
    }
}


