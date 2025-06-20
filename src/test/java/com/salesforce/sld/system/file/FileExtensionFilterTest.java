/*
 * Copyright (c) 2021, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.system.file;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FileExtensionFilterTest {

    private DefaultFileExtensionFilter sampleExtensionFilter = new DefaultFileExtensionFilter();
    private DefaultFileNameFilter sampleNameFilter = new DefaultFileNameFilter();

    private FileNameValidator pathValidator = new FileNameValidator(sampleExtensionFilter, sampleNameFilter, 256);


    @Parameters(name = "f1={0}, expect={1}")
    public static List<Object[]> params() {
        return Arrays.asList(new Object[][] {
                { "/foo/bar", false, false, false, false, false },
                { "/foo/bar.", false, false, false, false, false },
                { "/foo/bar..", false, false, true, false, false },
                { "/foo/bar.jpg", false, true, false, false, false },
                { "~/foo/bar.jpg", false, true, false, false, false },
                { "foo/bar.jpg", false, true, false, false, false },
                { "bar.jpg", false, true, false, true, false },
                { "/foo/bar.php.jpg", false, true, true, false, false },
                { "/foo/bar.jpg.php", false, false, true, false, false },
                { "~/foo/bar.php.jpg", false, true, true, false, false },
                { "foo/bar.php.jpg", false, true, true, false, false },
                { "bar.php.jpg", false, true, true, true, false },
                { "/foo/bar.php.j pg", false, false, true, false, false },
                { "/foo/bar .php.jpg", false, true, true, false, false },
                { "/foo/bar.jp/g", false, false, false, false, false },
                { "/foo/bar.jp\ng", false, false, false, false, false },
                { "/foo/bar.j;pg", false, false, false, false, false },
                { "/foo/b?ar.jpg", false, true, false, false, false },
                { "~/foo/b,ar.jpg", false, true, false, false, false },
                { "foo/ba*r.jpg", false, true, false, false, false },
                { "/foo/ba\nr.php.j pg", false, false, true, false, false },
                { "/foo/b<ar .php.jpg", false, true, true, false, false },
                { "/foo/b/ar.jp/g", false, false, false, false, false },
                { "/foo/ba?r.jpg", false, true, false, false, false },
                { "/foo/ba,r.jpg", false, true, false, false, false },
                { "/foo/ba*r.jpg", false, true, false, false, false },
                { "/foo/ba/r.jpg", false, true, false, false, false },
                { "test/dir. php .jpg", false, true, true, false, false },
                { "/foo/bar.xml", false, false, false, false, true },
                { "/foo/bar.csv", false, false, false, false, true },
                { "/foo/bar.php.csv", false, false, true, false, true },
                { "/foo/bar.php.json", false, false, true, false, true },
                { "foo/ba*r.xml", false, false, false, false, true },
                { "foo/ba*r.json", false, false, false, false, true },
        });
    }

    @Parameter()
    public String testPath;

    @Parameter(1)
    public boolean isAllowedExtension;

    @Parameter(2)
    public boolean isImageFileExtension;

    @Parameter(3)
    public boolean isDoubleExtension;

    @Parameter(4)
    public boolean fileNameOnly;

    @Parameter(5)
    public boolean isDataFileExtension;


    @Test
    public void testDoubleExtension() {
        boolean retDouble = pathValidator.isDoubleExtension(testPath);
        assertEquals(isDoubleExtension, retDouble);
    }

    @Test
    public void testSingleExtension() {
        boolean retSingle = pathValidator.isSingleExtension(testPath);
        assertEquals(!isDoubleExtension, retSingle);
    }

    @Test
    public void testisFileNameOnlyPath() {
        boolean isFileNameOnlyPath = pathValidator.isFileNameOnlyPath(testPath);
        assertEquals(fileNameOnly, isFileNameOnlyPath);
    }

    @Test
    public void testHelperFunctions() {
        boolean retSingle = pathValidator.isSingleExtension(testPath);
        boolean retDouble = pathValidator.isDoubleExtension(testPath);
        boolean retAllowed = pathValidator.isAllowedExtension(testPath);
        String extension = pathValidator.getExtension(testPath);

        if(retSingle)
        {
            assertEquals(isAllowedExtension, retAllowed);
        }

        if(extension.length() > 0)
        {
            assertEquals(retDouble , !retSingle);
        }
    }

}
