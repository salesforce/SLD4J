/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.validation.encoders.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.salesforce.sld.validation.encoders.impl.JSONManipulator;
import org.junit.Test;

import com.salesforce.sld.validation.encoders.impl.json.JSONValueManipulator;

public class JSONManipulatorTest
{
    private final JSONManipulator json = new JSONValueManipulator();

    /**
     * Test that large Unicode characters are encoded properly
     */
    @Test
    public void testUnicode()
    {
        Character c = '\u2222';
        assertEquals( "\u2222", this.json.getCorrectCharacter( c ) );
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
            new SimpleEntry<Character, String>( '"', "\\\"" ), new SimpleEntry<Character, String>( '\\', "\\\\" ),
            new SimpleEntry<Character, String>( '/', "\\/" ) );

        for ( SimpleEntry<Character, String> escape : escapes )
        {
            Character orig = escape.getKey();
            String expect = escape.getValue();
            String actual = this.json.getCorrectCharacter( orig );
            assertEquals( expect, actual );
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
                this.json.getCorrectCharacter( (char) i );
            }
        }
        catch ( Exception e )
        {
            fail( "Exception throw in testNoExceptions - " + e.getMessage() );
        }

    }

    class JSONSub
        extends JSONManipulator
    {
        public JSONSub( Character[] escapeCharacters )
        {
            super( new HashSet<Character>( Arrays.asList( escapeCharacters ) ) );
        }
    }

    @Test
    public void testSubclass()
    {
        JSONManipulator json = new JSONSub( new Character[] { '!' } );
        assertEquals( json.getCorrectCharacter( '!' ), "\\!" );
        assertEquals( json.getCorrectCharacter( 'a' ), "a" );

        Character[] immune = new Character[] {};
        JSONManipulator json2 = new JSONSub( immune );
        assertNotEquals( json2.getCorrectCharacter( '!' ), "\\!" );
        assertEquals( json2.getCorrectCharacter( 'a' ), "a" );
    }
}
