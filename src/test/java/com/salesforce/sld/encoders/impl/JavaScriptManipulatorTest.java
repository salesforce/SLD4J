/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.encoders.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.salesforce.sld.encoders.impl.js.JavaScriptAttrManipulator;
import com.salesforce.sld.encoders.impl.js.JavaScriptBlockManipulator;
import com.salesforce.sld.encoders.impl.js.JavaScriptHTMLManipulator;
import com.salesforce.sld.encoders.impl.js.JavaScriptSourceManipulator;

public class JavaScriptManipulatorTest
{

    private final JavaScriptManipulator html = new JavaScriptHTMLManipulator();

    private final JavaScriptManipulator attr = new JavaScriptAttrManipulator();

    private final JavaScriptManipulator blck = new JavaScriptBlockManipulator();

    private final JavaScriptManipulator src = new JavaScriptSourceManipulator();

    private final List<JavaScriptManipulator> jslist = Arrays.asList( html, attr, blck, src );

    /**
     * Test that large Unicode characters are encoded properly
     */
    @Test
    public void testUnicode()
    {
        Character c = '\u2222';
        assertEquals( "\u2222", this.html.getCorrectCharacter( c ) );
    }

    /**
     * Test that string escaping works correctly
     */
    @Test
    public void testEscape()
    {
        List<SimpleEntry<Character, String>> escapes = Arrays.asList( new SimpleEntry<Character, String>( '\b', "\\b" ),
            new SimpleEntry<Character, String>( '\t', "\\t" ), new SimpleEntry<Character, String>( '\n', "\\n" ),
            new SimpleEntry<Character, String>( '\f', "\\f" ), new SimpleEntry<Character, String>( '\r', "\\r" ),
            new SimpleEntry<Character, String>( '\\', "\\\\" ) );

        for ( JavaScriptManipulator manip : this.jslist )
        {
            for ( SimpleEntry<Character, String> escape : escapes )
            {
                Character orig = escape.getKey();
                String expect = escape.getValue();
                String actual = manip.getCorrectCharacter( orig );
                assertEquals( expect, actual );
            }
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
                for ( JavaScriptManipulator manip : this.jslist )
                {
                    manip.getCorrectCharacter( (char) i );
                }
            }
        }
        catch ( Exception e )
        {
            fail( "Exception throw in testNoExceptions - " + e.getMessage() );
        }
    }

    class JSSub
        extends JavaScriptManipulator
    {

        protected JSSub( Character[] ignoreCharacters, Character[] escapeCharacters )
        {
            super( new HashSet<Character>( Arrays.asList( ignoreCharacters ) ),
                new HashSet<Character>( Arrays.asList( escapeCharacters ) ) );
        }

    }

    @Test
    public void testSubclass()
    {
        JavaScriptManipulator jsm = new JSSub( new Character[] { '?' }, new Character[] { '!' } );
        assertEquals( jsm.getCorrectCharacter( '?' ), "?" );
        assertEquals( jsm.getCorrectCharacter( '!' ), "\\!" );

        Character[] ignore = new Character[] {};
        JavaScriptManipulator jsm2 = new JSSub( new Character[] { '?' }, ignore );
        assertEquals( jsm2.getCorrectCharacter( '?' ), "?" );
        assertNotEquals( jsm2.getCorrectCharacter( '!' ), "\\!" );

        Character[] escape = new Character[] {};
        JavaScriptManipulator jsm3 = new JSSub( escape, new Character[] { '!' } );
        assertNotEquals( jsm3.getCorrectCharacter( '?' ), "?" );
        assertEquals( jsm3.getCorrectCharacter( '!' ), "\\!" );
    }
}
