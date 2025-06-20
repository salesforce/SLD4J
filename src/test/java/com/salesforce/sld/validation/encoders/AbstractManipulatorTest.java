/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.validation.encoders;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.salesforce.sld.validation.encoders.impl.CDATAManipulator;
import com.salesforce.sld.validation.encoders.impl.html.HTMLContentManipulator;
import com.salesforce.sld.validation.encoders.impl.html.HTMLDoubleQuotedAttrManipulator;
import com.salesforce.sld.validation.encoders.impl.html.HTMLSingleQuotedAttrManipulator;
import com.salesforce.sld.validation.encoders.impl.html.HTMLUnquotedAttrManipulator;
import com.salesforce.sld.validation.encoders.impl.js.JavaScriptAttrManipulator;
import com.salesforce.sld.validation.encoders.impl.js.JavaScriptBlockManipulator;
import com.salesforce.sld.validation.encoders.impl.js.JavaScriptHTMLManipulator;
import com.salesforce.sld.validation.encoders.impl.js.JavaScriptSourceManipulator;
import com.salesforce.sld.validation.encoders.impl.json.JSONValueManipulator;
import com.salesforce.sld.validation.encoders.impl.uri.URILenientManipulator;
import com.salesforce.sld.validation.encoders.impl.uri.URIStrictManipulator;
import com.salesforce.sld.validation.encoders.impl.xml.XMLCommentContentManipulator;
import com.salesforce.sld.validation.encoders.impl.xml.XMLContentManipulator;
import com.salesforce.sld.validation.encoders.impl.xml.XMLDoubleQuotedAttrManipulator;
import com.salesforce.sld.validation.encoders.impl.xml.XMLSingleQuotedAttrManipulator;

@RunWith( Parameterized.class )
public class AbstractManipulatorTest
{
    @Parameters( name = "{0}" )
    public static List<Object[]> manipulators()
    {
        List<Object[]> params = new ArrayList<Object[]>();

        params.add( new Object[] { new CDATAManipulator() } );
        params.add( new Object[] { new HTMLContentManipulator() } );
        params.add( new Object[] { new HTMLSingleQuotedAttrManipulator() } );
        params.add( new Object[] { new HTMLDoubleQuotedAttrManipulator() } );
        params.add( new Object[] { new HTMLUnquotedAttrManipulator() } );
        params.add( new Object[] { new JavaScriptHTMLManipulator() } );
        params.add( new Object[] { new JavaScriptAttrManipulator() } );
        params.add( new Object[] { new JavaScriptBlockManipulator() } );
        params.add( new Object[] { new JavaScriptSourceManipulator() } );
        params.add( new Object[] { new JSONValueManipulator() } );
        params.add( new Object[] { new URILenientManipulator() } );
        params.add( new Object[] { new URIStrictManipulator() } );
        params.add( new Object[] { new XMLContentManipulator() } );
        params.add( new Object[] { new XMLSingleQuotedAttrManipulator() } );
        params.add( new Object[] { new XMLDoubleQuotedAttrManipulator() } );
        params.add( new Object[] { new XMLCommentContentManipulator() } );
        return params;
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Parameter( 0 )
    public AbstractManipulator manipulator;

    @Test
    public void testNullEncode()
    {
        String result = manipulator.encode( null );
        assertNull( result );
    }

    @Test
    public void testNullEncodeWriter()
        throws IOException
    {
        StringWriter writer = new StringWriter();

        manipulator.encode( null, writer );
        assertTrue( writer.toString().equals( "" ) );
    }

    @Test
    public void testNullWriterEncode()
        throws IllegalArgumentException, IOException
    {
        this.exception.expect( IllegalArgumentException.class );

        manipulator.encode( "", null );
    }

    @Test
    public void testNullFilter()
    {
        String result = manipulator.filter( null );
        assertNull( result );
    }

    @Test
    public void testNullFilterWriter()
        throws IOException
    {
        StringWriter writer = new StringWriter();

        manipulator.filter( null, writer );
        assertTrue( writer.toString().equals( "" ) );
    }

    @Test
    public void testNullWriterFilter()
        throws IllegalArgumentException, IOException
    {
        this.exception.expect( IllegalArgumentException.class );

        manipulator.filter( "", null );
    }
}
