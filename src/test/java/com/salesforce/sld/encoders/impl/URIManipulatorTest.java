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

import com.salesforce.sld.encoders.impl.uri.URILenientManipulator;
import com.salesforce.sld.encoders.impl.uri.URIStrictManipulator;

public class URIManipulatorTest
{

    private final URIManipulator uri = new URILenientManipulator();

    private final URIManipulator strict = new URIStrictManipulator();


    /**
     * Test entities work for a few entities
     */
    @Test
    public void testPercentEncoding()
    {

        List<SimpleEntry<Character, String>> list = Arrays.asList( new SimpleEntry<Character, String>( (char) 33, "!" ),
            new SimpleEntry<Character, String>( (char) 45, "-" ), new SimpleEntry<Character, String>( (char) 95, "_" ),
            new SimpleEntry<Character, String>( (char) 46, "." ), new SimpleEntry<Character, String>( (char) 126, "~" ),
            new SimpleEntry<Character, String>( (char) 42, "*" ), new SimpleEntry<Character, String>( (char) 39, "\'" ),
            new SimpleEntry<Character, String>( (char) 40, "(" ), new SimpleEntry<Character, String>( (char) 41, ")" ),
            new SimpleEntry<Character, String>( (char) 64, "%40" ), /* @ */
            new SimpleEntry<Character, String>( (char) 125, "%7d" ) /* } */
        );

        for ( SimpleEntry<Character, String> entry : list )
        {
            assertEquals( entry.getValue(), this.uri.getCorrectCharacter( entry.getKey() ) );
        }
    }

    /**
     * Test entities work for a few entities
     */
    @Test
    public void testStrictPercentEncoding()
    {

        List<SimpleEntry<Character, String>> list = Arrays.asList(
            new SimpleEntry<Character, String>( (char) 33, "%21" ),
            new SimpleEntry<Character, String>( (char) 45, "-" ), new SimpleEntry<Character, String>( (char) 95, "_" ),
            new SimpleEntry<Character, String>( (char) 46, "." ), new SimpleEntry<Character, String>( (char) 126, "~" ),
            new SimpleEntry<Character, String>( (char) 42, "%2a" ),
            new SimpleEntry<Character, String>( (char) 39, "%27" ),
            new SimpleEntry<Character, String>( (char) 40, "%28" ),
            new SimpleEntry<Character, String>( (char) 41, "%29" ),
            new SimpleEntry<Character, String>( (char) 64, "%40" ), /* @ */
            new SimpleEntry<Character, String>( (char) 125, "%7d" ) /* } */
        );

        for ( SimpleEntry<Character, String> entry : list )
        {
            assertEquals( entry.getValue(), this.strict.getCorrectCharacter( entry.getKey() ) );
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
                this.uri.getCorrectCharacter( (char) i );
                this.strict.getCorrectCharacter( (char) i );
            }
        }
        catch ( Throwable e )
        {
            fail( "Exception throw in testNoExceptions - " + e.getMessage() );
        }

    }

    class URISub
        extends URIManipulator
    {

        public URISub( Character[] immuneCharacters )
        {
            super( new HashSet<Character>( Arrays.asList( immuneCharacters ) ) );
        }

    }

    @Test
    public void testSubclass()
    {
        URIManipulator uri = new URISub( new Character[] { '!' } );
        assertEquals( uri.getCorrectCharacter( '!' ), "!" );
        assertEquals( uri.getCorrectCharacter( 'a' ), "a" );

        Character[] immune = new Character[] {};
        URIManipulator uri2 = new URISub( immune );
        assertNotEquals( uri2.getCorrectCharacter( '!' ), "!" );
        assertEquals( uri2.getCorrectCharacter( 'a' ), "a" );
    }
}
