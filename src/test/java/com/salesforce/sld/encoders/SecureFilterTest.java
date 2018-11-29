/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.encoders;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

import com.salesforce.sld.encoders.SecureFilter;

public class SecureFilterTest
{
    SecureFilter secureFilter = new SecureFilter();
    
    @Test
    public void CDATATest()
    {
        String CDATA1 =
            "<!--this! is/ a; comment: --><foo attribute=value>text</foo><bar attribute=\"doublevalue\">text2</bar><baz attribute='singlevalue'>)(*#$!@#?</baz>";
        StringWriter sw = new StringWriter( CDATA1.length() );

        assertEquals( "filterCDATA positive test failed", CDATA1, secureFilter.filterCDATAContent( CDATA1 ) );

        secureFilter.filterCDATAContent( CDATA1, sw );
        assertEquals( "filterCDATA positive test failed", CDATA1, sw.toString() );

        String CDATA2 = "foo]]]]>]]";
        sw = new StringWriter( CDATA2.length() );
        String expected = "foo]]]]";
        assertEquals( "filterCDATA negative test failed", expected, secureFilter.filterCDATAContent( CDATA2 ) );

        secureFilter.filterCDATAContent( CDATA2, sw );
        assertEquals( "filterCDATA negative test failed", expected, sw.toString() );
    }
    
    @Test
    public void HTMLTest()
    {
        String htmlTest =
            "<!--this! is/ a; comment: --><foo attribute=value>text</foo><bar attribute=\"doublevalue\">text2</bar><baz attribute='singlevalue'>)(*#$!@#?</baz>";
        StringWriter sw = new StringWriter( htmlTest.length() * 2 );

        String htmlContent =
            "!--this! is/ a; comment: --foo attribute=valuetext/foobar attribute=doublevaluetext2/barbaz attribute=singlevalue)(*#$!@#?/baz";
        assertEquals( "filterHTMLContent failed", htmlContent, secureFilter.filterHtmlContent( htmlTest ) );

        secureFilter.filterHtmlContent( htmlTest, sw );
        assertEquals( "filterHTMLContent failed", htmlContent, sw.toString() );

        sw = new StringWriter( htmlTest.length() * 2 );
        String htmlDouble =
            "!--this! is/ a; comment: --foo attribute=valuetext/foobar attribute=doublevaluetext2/barbaz attribute='singlevalue')(*#$!@#?/baz";
        assertEquals( "filterHtmlInDoubleQuoteAttribute failed", htmlDouble,
            secureFilter.filterHtmlInDoubleQuoteAttribute( htmlTest ) );

        secureFilter.filterHtmlInDoubleQuoteAttribute( htmlTest, sw );
        assertEquals( "filterHtmlInDoubleQuoteAttribute failed", htmlDouble, sw.toString() );

        sw = new StringWriter( htmlTest.length() * 2 );
        String htmlSingle =
            "!--this! is/ a; comment: --foo attribute=valuetext/foobar attribute=\"doublevalue\"text2/barbaz attribute=singlevalue)(*#$!@#?/baz";
        assertEquals( "filterHtmlInSingleQuoteAttribute failed", htmlSingle,
            secureFilter.filterHtmlInSingleQuoteAttribute( htmlTest ) );

        secureFilter.filterHtmlInSingleQuoteAttribute( htmlTest, sw );
        assertEquals( "filterHtmlInSingleQuoteAttribute failed", htmlSingle, sw.toString() );

        sw = new StringWriter( htmlTest.length() * 2 );
        String htmlUnq =
            "!--this!is/a;comment:--fooattribute=valuetext/foobarattribute=doublevaluetext2/barbazattribute=singlevalue)(*#$!@#?/baz";
        assertEquals( "filterHtmlUnquotedAttribute failed", htmlUnq,
            secureFilter.filterHtmlUnquotedAttribute( htmlTest ) );

        secureFilter.filterHtmlUnquotedAttribute( htmlTest, sw );
        assertEquals( "filterHtmlUnquotedAttribute failed", htmlUnq, sw.toString() );

    }

    @Test
    public void XMLTest()
    {
        String xmlTest =
            "<!--this! is/ a; comment: --><foo attribute=value>text</foo><bar attribute=\"doublevalue\">text2</bar><baz attribute='singlevalue'>)(*#$!@#?</baz>";
        StringWriter sw = new StringWriter( xmlTest.length() * 2 );

        String xmlContent =
            "--this is a; comment: --foo attributevaluetextfoobar attributedoublevaluetext2barbaz attributesinglevalue)(baz";
        assertEquals( "filterXmlContent failed", xmlContent, secureFilter.filterXmlContent( xmlTest ) );

        secureFilter.filterXmlContent( xmlTest, sw );
        assertEquals( "filterXmlContent failed", xmlContent, sw.toString() );

        sw = new StringWriter( xmlTest.length() * 2 );
        String xmlDouble =
            "--this is a; comment: --foo attributevaluetextfoobar attributedoublevaluetext2barbaz attribute'singlevalue')(baz";
        assertEquals( "filterXmlInDoubleQuoteAttribute failed", xmlDouble,
            secureFilter.filterXmlInDoubleQuoteAttribute( xmlTest ) );

        secureFilter.filterXmlInDoubleQuoteAttribute( xmlTest, sw );
        assertEquals( "filterXmlInDoubleQuoteAttribute failed", xmlDouble, sw.toString() );

        sw = new StringWriter( xmlTest.length() * 2 );
        String xmlSingle =
            "--this is a; comment: --foo attributevaluetextfoobar attribute\"doublevalue\"text2barbaz attributesinglevalue)(baz";
        assertEquals( "filterXmlInSingleQuoteAttribute failed", xmlSingle,
            secureFilter.filterXmlInSingleQuoteAttribute( xmlTest ) );

        secureFilter.filterXmlInSingleQuoteAttribute( xmlTest, sw );
        assertEquals( "filterXmlInSingleQuoteAttribute failed", xmlSingle, sw.toString() );

        sw = new StringWriter( xmlTest.length() * 2 );
        String xmlComment =
            "<!this! is/ a; comment: ><foo attribute=value>text</foo><bar attribute=\"doublevalue\">text2</bar><baz attribute='singlevalue'>)(*#$!@#?</baz>";
        assertEquals( "filterXmlComment failed", xmlComment, secureFilter.filterXmlCommentContent( xmlTest ) );

        secureFilter.filterXmlCommentContent( xmlTest, sw );
        assertEquals( "filterXmlComment failed", xmlComment, sw.toString() );

    }

    @Test
    public void JavaScriptTest()
    {
        String javascriptTest =
            "console.log(\"Log Message!\");\r\n $(ajax).postMessage('foo.com');\nvar x = 123+14*82/12-6;";
        StringWriter sw = new StringWriter( javascriptTest.length() * 2 );

        String jsHTML = "console.log(Log Message!); (ajax).postMessage(foo.com);var x = 123+14*82126;";
        assertEquals( "filterJavaScriptInHTML failed", jsHTML, secureFilter.filterJavaScriptInHTML( javascriptTest ) );

        secureFilter.filterJavaScriptInHTML( javascriptTest, sw );
        assertEquals( "filterJavaScriptInHTML failed", jsHTML, sw.toString() );

        sw = new StringWriter( javascriptTest.length() * 2 );
        String jsAttr = "console.log(Log Message!); (ajax).postMessage(foo.com);var x = 123+14*82/12-6;";
        assertEquals( "filterJavaScriptInAttribute failed", jsAttr,
            secureFilter.filterJavaScriptInAttribute( javascriptTest ) );

        secureFilter.filterJavaScriptInAttribute( javascriptTest, sw );
        assertEquals( "filterJavaScriptInAttribute failed", jsAttr, sw.toString() );

        sw = new StringWriter( javascriptTest.length() * 2 );
        String jsBlock = "console.log(Log Message!); (ajax).postMessage(foo.com);var x = 123+14*82126;";
        assertEquals( "filterJavaScriptInBlock failed", jsBlock,
            secureFilter.filterJavaScriptInBlock( javascriptTest ) );

        secureFilter.filterJavaScriptInBlock( javascriptTest, sw );
        assertEquals( "filterJavaScriptInBlock failed", jsBlock, sw.toString() );

        sw = new StringWriter( javascriptTest.length() * 2 );
        String jsSource = "console.log(Log Message!); (ajax).postMessage(foo.com);var x = 123+14*82/12-6;";
        assertEquals( "filterJavaScriptInSource failed", jsSource,
            secureFilter.filterJavaScriptInSource( javascriptTest ) );

        secureFilter.filterJavaScriptInSource( javascriptTest, sw );
        assertEquals( "filterJavaScriptInSource failed", jsSource, sw.toString() );

    }

    @Test
    public void JSONTest()
    {
        String jsonValues = "\"}{\"CustomData\":[\"foo bar\"]}";
        StringWriter sw = new StringWriter( jsonValues.length() * 2 );

        String json = "CustomDatafoobar";
        assertEquals( "filterJSONValue failed", json, secureFilter.filterJSONValue( jsonValues ) );

        secureFilter.filterJSONValue( jsonValues, sw );
        assertEquals( "filterJSONValue failed", json, sw.toString() );
    }

    @Test
    public void URITest()
    {
        String URI = "?foo=bar&test=^42@314*(&SF&Ts=+~\u0732";
        StringWriter sw = new StringWriter( URI.length() * 2 );

        String uriComp = "foobartest42314*(SFTs~";
        assertEquals( "filterUriComponent failed", uriComp, secureFilter.filterUriComponent( URI ) );

        secureFilter.filterUriComponent( URI, sw );
        assertEquals( "filterUriComponent failed", uriComp, sw.toString() );

        sw = new StringWriter( URI.length() * 2 );
        String uriStrict = "foobartest42314SFTs~";
        assertEquals( "filterUriComponentStrict failed", uriStrict, secureFilter.filterUriComponentStrict( URI ) );

        secureFilter.filterUriComponentStrict( URI, sw );
        assertEquals( "filterUriComponentStrict failed", uriStrict, sw.toString() );
    }
}
