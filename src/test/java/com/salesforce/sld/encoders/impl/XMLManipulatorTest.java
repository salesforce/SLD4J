/*
 * Copyright 2015 Demandware Inc. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
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

import com.salesforce.sld.encoders.impl.xml.XMLCommentContentManipulator;
import com.salesforce.sld.encoders.impl.xml.XMLContentManipulator;
import com.salesforce.sld.encoders.impl.xml.XMLDoubleQuotedAttrManipulator;
import com.salesforce.sld.encoders.impl.xml.XMLSingleQuotedAttrManipulator;

public class XMLManipulatorTest
{

    private final XMLManipulator comMan = new XMLCommentContentManipulator();

    private final XMLManipulator conMan = new XMLContentManipulator();

    private final XMLManipulator dblMan = new XMLDoubleQuotedAttrManipulator();

    private final XMLManipulator sglMan = new XMLSingleQuotedAttrManipulator();

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
                new SimpleEntry<Character, String>( (char) 62, "&gt;" ) /* greater-than sign */
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
        String replaceHex = "";
        for ( int i = 0x80; i <= 0x9f; i++ )
        {
            if ( i == 0x85 )
            {
                continue;
            }
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
                this.comMan.getCorrectCharacter( (char) i );
                this.conMan.getCorrectCharacter( (char) i );
                this.dblMan.getCorrectCharacter( (char) i );
                this.sglMan.getCorrectCharacter( (char) i );
            }
        }
        catch ( Throwable e )
        {
            fail( "Exception throw in testNoExceptions - " + e.getMessage() );
        }

    }

    class XMLSub
        extends XMLManipulator
    {

        public XMLSub( Character[] immuneCharacters )
        {
            super( new HashSet<Character>( Arrays.asList( immuneCharacters ) ) );
        }

    }

    @Test
    public void testSubclass()
    {
        XMLManipulator xml = new XMLSub( new Character[] { '!' } );
        assertEquals( xml.getCorrectCharacter( '!' ), "!" );
        assertEquals( xml.getCorrectCharacter( '>' ), "&gt;" );

        Character[] immune = new Character[] {};
        XMLManipulator xml2 = new XMLSub( immune );
        assertNotEquals( xml2.getCorrectCharacter( '!' ), "!" );
        assertEquals( xml2.getCorrectCharacter( '>' ), "&gt;" );
    }
}
