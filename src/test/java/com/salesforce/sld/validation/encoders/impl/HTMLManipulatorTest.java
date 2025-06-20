/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.validation.encoders.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.salesforce.sld.validation.encoders.impl.html.HTMLContentManipulator;
import com.salesforce.sld.validation.encoders.impl.html.HTMLDoubleQuotedAttrManipulator;
import com.salesforce.sld.validation.encoders.impl.html.HTMLSingleQuotedAttrManipulator;
import com.salesforce.sld.validation.encoders.impl.html.HTMLUnquotedAttrManipulator;

public class HTMLManipulatorTest
{

    private final HTMLManipulator conMan = new HTMLContentManipulator();

    private final HTMLManipulator dblMan = new HTMLDoubleQuotedAttrManipulator();

    private final HTMLManipulator sglMan = new HTMLSingleQuotedAttrManipulator();

    private final HTMLManipulator noqMan = new HTMLUnquotedAttrManipulator();

    /**
     * Test entities work for a few entities
     */
    @Test
    public void testEntityEncoding()
    {

        List<SimpleEntry<Character, String>> list =
            Arrays.asList( new SimpleEntry<Character, String>( (char) 34, "&quot;" ), /* quotation mark */
                new SimpleEntry<Character, String>( (char) 38, "&amp;" ), /* ampersand */
                new SimpleEntry<Character, String>( (char) 60, "&lt;" ), /* less-than sign */
                new SimpleEntry<Character, String>( (char) 62, "&gt;" ), /* greater-than sign */
                new SimpleEntry<Character, String>( (char) 160, "&nbsp;" ) /* no-break space */
            );

        for ( SimpleEntry<Character, String> entry : list )
        {
            assertEquals( entry.getValue(), this.conMan.getCorrectCharacter( entry.getKey() ) );
        }
    }

    /**
     * Test replacement character is used for odd control characters
     */
    @Test
    public void testReplacementCharacters()
    {
        String replaceHex = "&#xfffd;";
        for ( int i = 0x80; i <= 0x9f; i++ )
        {
            assertEquals( replaceHex, this.conMan.getCorrectCharacter( (char) i ) );
        }
    }

    /**
     * Total Sanity Test to make sure test code doesn't explode
     */
    @Test
    public void testNoExceptions()
    {
        try
        {
            for ( int i = 0; i < Character.MAX_CODE_POINT; i++ )
            {
                this.conMan.getCorrectCharacter( (char) i );
                this.dblMan.getCorrectCharacter( (char) i );
                this.sglMan.getCorrectCharacter( (char) i );
                this.noqMan.getCorrectCharacter( (char) i );
            }
        }
        catch ( Exception e )
        {
            fail( "Exception throw in testNoExceptions - " + e.getMessage() );
        }

    }

    class HTMLSub
        extends HTMLManipulator
    {

        protected HTMLSub( Character[] immunes )
        {
            super( new HashSet<Character>( Arrays.asList( immunes ) ) );
        }

    }

    @Test
    public void testSubclass()
    {
        HTMLManipulator html = new HTMLSub( new Character[] { '!' } );
        assertEquals( html.getCorrectCharacter( '!' ), "!" );
        assertEquals( html.getCorrectCharacter( '>' ), "&gt;" );
    }
}
