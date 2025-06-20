/*
 * Copyright (c) 2021, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.system.file;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FileNameFilterTest {

    private DefaultFileExtensionFilter sampleExtensionFilter = new DefaultFileExtensionFilter();
    private DefaultFileNameFilter sampleFileFilter = new DefaultFileNameFilter();

    private FileNameValidator pathValidatorFilters = new FileNameValidator(sampleExtensionFilter, sampleFileFilter, 256);

    private FileNameValidator shortValidatorFilters = new FileNameValidator(sampleExtensionFilter, sampleFileFilter, 10);


    @Before
    public void initialize() {
        sampleFileFilter.addPattern("[a-zA-Z0-9()_.,-]*");
        sampleExtensionFilter.addExtension(".xml");
        sampleExtensionFilter.addExtension(".json");
        sampleExtensionFilter.addExtension(".csv");
    }

    @Parameters(name = "f1={0}, expect={1}")
    public static List<Object[]> params() {
        return Arrays.asList(new Object[][]{

                {"/foo/bar", true, true, false, false}, // VALID: all lowercase
                {"/foo/bARbarbar", true, true, false, false}, // VALID: lowercase and uppercase
                {"/foo/bar1234567890", true, false, false, false}, // VALID: lowercase and numbers
                {"/foo/()", true, true, false, false}, // VALID: parentheses
                {"/bar/foo_", true, true, false, false}, // VALID: lowercase and underscore
                {"/foo/b,ar", true, true, false, false}, // VALID: lowercase and comma
                {"/foo/b-ar", true,true, false, false}, // VALID: lowercase and dash
                {"/bar/foo.", true, true, false, false}, // VALID: lowercase and period
                {"bar.xml", true, true, true, true}, // VALID: lowercase
                {"/bar/f*oo", false, false, false, false}, // INVALID: uses "*"
                {"/bar/<foo", false, false, false, false}, // INVALID: uses "<"
                {"/bar/foo>", false, false, false, false}, //INVALID: uses ">"
                {"/foo/foo^", false, false, false, false}, //INVALID: uses "^"
                {"/foo/bar#", false, false, false, false}, //INVALID: uses "#"
                {"aiuh98uqaeijnwegoij109IJDNOQUEosijdfoiwepojd08u98vsijni3ubr98ho8go87g887g3817g9481y938769a8fy08y" +
                        "9879898y7913uhodbuoaihoa7gweoiruabieuSDAWDV23iubsdu8hweohifq298haskjbxciuagefaiufousdfouqe" +
                        "asdovibqwoeufbaosudyvADFWEFARGBERTH13408y9ubfakejbfaiuy7g3sdiubaweub1238ysdivubaweug", false, false, false, false},
                {"aaaadddddafijiauhiuhaioiauhefiuhdkjfhaskldjfhaklsjdhfklasjdhflkasjhdflasjhdflkasjdhflkasjhdflkasjhdflkaj"
                        + "asjdlaksdjfljwoaijflkdlkcsldkfjalwiejflskdjfsd,mlaskjflaiwejfalsdkfjlskdclskdjfoaiwejflasdkjfldxklksd" +
                        "aisdjfhiwuehfksdjfahweifuhisajdnvkjsbgiwuehfiausdhfkjshfiauwebfisahdfsehfiawefiasjnfiaseifhawoieghaisu" +
                        "asuehasdfaweuhfisdhfkajsneifuhasiduhajsdfniauwegiawuhegoasdvkajsnfkauwheifauwhefojsndffiaushdof", false, false, false, false},
                // INVALID: over max num (256) characters
                {"foo*.xml", false, false, true, true},
                {"foo", true, true, false, false},
                {"FOO", true, true, false, false},
                {"foo123456789", true, false, false, false},
                {"foo()", true, true, false, false},
                {"f,oo", true, true, false, false},
                {"foo_", true, true, false, false},
                {"foo-", true, true, false, false},
                {"foo.", true, true, false, false},
                {"foo*", false, false, false, false},
                {"f>oo", false, false, false, false},
                {"f>oo", false, false, false, false},
                {"foo^", false, false, false, false},
                {"foo%", false, false, false, false},
                {"foo.jp*g", false, false, false, false},
                {"foo.jpeg.xml", true, false, true, false},
                {"foo.jp*g.xml", false, false, true, false},
                {"foo.json.xml", true, false, true, false},
                {"foo.json<.xml", false, false, true, false},
                {"foo1", true, true, false, false}

        });
    }

    @Parameter()
    public String testPath;

    @Parameter(1)
    public boolean isAllowedName;

    @Parameter(2)
    public boolean isAllowedNameShort;

    @Parameter(3)
    public boolean isAllowedMultipleExtensions;

    @Parameter(4)
    public boolean multipleExtensionsNotAllowed;

    @Test
    public void testValidNameFilter() {
        boolean ret = pathValidatorFilters.isAllowedFileName(testPath);
        assertEquals(isAllowedName, ret);

    }

    @Test
    public void testShortNameUsingFilters() {
        boolean ret = shortValidatorFilters.isAllowedFileName(testPath);
        assertEquals(isAllowedNameShort, ret);
    }

    @Test
    public void testMultipleExtensions() {
        boolean ret = pathValidatorFilters.isAllowedExtension( testPath, true );
        assertEquals( isAllowedMultipleExtensions, ret );
    }

    @Test
    public void testMultipleExtensionsNotAllowed() {
        boolean ret = pathValidatorFilters.isAllowedExtension( testPath );
        assertEquals( multipleExtensionsNotAllowed, ret );
    }


}
