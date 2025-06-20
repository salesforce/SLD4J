/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.validation.encoders;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

public class SecureEncoderTest
{
    SecureEncoder secureEncoder = new SecureEncoder();
    
    @Test
    public void CDATATest()
    {
        String CDATA1 =
            "<!--this! is/ a; comment: --><foo attribute=value>text</foo><bar attribute=\"doublevalue\">text2</bar><baz attribute='singlevalue'>)(*#$!@#?</baz>";
        StringWriter sw = new StringWriter( CDATA1.length() );

        assertEquals( "encodeCDATA positive test failed", CDATA1, secureEncoder.encodeCDATAContent( CDATA1 ) );

        secureEncoder.encodeCDATAContent( CDATA1, sw );
        assertEquals( "encodeCDATA positive test failed", CDATA1, sw.toString() );

        String CDATA2 = "foo]]]]>]]";
        sw = new StringWriter( CDATA2.length() );
        String expected = "foo]]]]>]]<![CDATA[>]]";
        assertEquals( "encodeCDATA negative test failed", expected, secureEncoder.encodeCDATAContent( CDATA2 ) );

        secureEncoder.encodeCDATAContent( CDATA2, sw );
        assertEquals( "encodeCDATA negative test failed", expected, sw.toString() );
    }

    @Test
    public void HTMLTest()
    {
        String htmlTest =
            "<!--this! is/ a; comment: --><foo attribute=value>text</foo><bar attribute=\"doublevalue\">text2</bar><baz attribute='singlevalue'>)(*#$!@#?</baz>";
        StringWriter sw = new StringWriter( htmlTest.length() * 2 );

        String htmlContent =
            "&lt;!--this! is/ a; comment: --&gt;&lt;foo attribute=value&gt;text&lt;/foo&gt;&lt;bar attribute=&quot;doublevalue&quot;&gt;text2&lt;/bar&gt;&lt;baz attribute=&#x27;singlevalue&#x27;&gt;)(*#$!@#?&lt;/baz&gt;";
        assertEquals( "encodeHTMLContent failed", htmlContent, secureEncoder.encodeHtmlContent( htmlTest ) );

        secureEncoder.encodeHtmlContent( htmlTest, sw );
        assertEquals( "encodeHTMLContent failed", htmlContent, sw.toString() );

        sw = new StringWriter( htmlTest.length() * 2 );
        String htmlDouble =
            "&lt;!--this! is/ a; comment: --&gt;&lt;foo attribute=value&gt;text&lt;/foo&gt;&lt;bar attribute=&quot;doublevalue&quot;&gt;text2&lt;/bar&gt;&lt;baz attribute='singlevalue'&gt;)(*#$!@#?&lt;/baz&gt;";
        assertEquals( "encodeHtmlInDoubleQuoteAttribute failed", htmlDouble,
            secureEncoder.encodeHtmlInDoubleQuoteAttribute( htmlTest ) );

        secureEncoder.encodeHtmlInDoubleQuoteAttribute( htmlTest, sw );
        assertEquals( "encodeHtmlInDoubleQuoteAttribute failed", htmlDouble, sw.toString() );

        sw = new StringWriter( htmlTest.length() * 2 );
        String htmlSingle =
            "&lt;!--this! is/ a; comment: --&gt;&lt;foo attribute=value&gt;text&lt;/foo&gt;&lt;bar attribute=\"doublevalue\"&gt;text2&lt;/bar&gt;&lt;baz attribute=&#x27;singlevalue&#x27;&gt;)(*#$!@#?&lt;/baz&gt;";
        assertEquals( "encodeHtmlInSingleQuoteAttribute failed", htmlSingle,
            secureEncoder.encodeHtmlInSingleQuoteAttribute( htmlTest ) );

        secureEncoder.encodeHtmlInSingleQuoteAttribute( htmlTest, sw );
        assertEquals( "encodeHtmlInSingleQuoteAttribute failed", htmlSingle, sw.toString() );

        sw = new StringWriter( htmlTest.length() * 2 );
        String htmlUnq =
            "&lt;!--this!&#x20;is/&#x20;a;&#x20;comment:&#x20;--&gt;&lt;foo&#x20;attribute=value&gt;text&lt;/foo&gt;&lt;bar&#x20;attribute=&quot;doublevalue&quot;&gt;text2&lt;/bar&gt;&lt;baz&#x20;attribute=&#x27;singlevalue&#x27;&gt;)(*#$!@#?&lt;/baz&gt;";
        assertEquals( "encodeHtmlUnquotedAttribute failed", htmlUnq,
            secureEncoder.encodeHtmlUnquotedAttribute( htmlTest ) );

        secureEncoder.encodeHtmlUnquotedAttribute( htmlTest, sw );
        assertEquals( "encodeHtmlUnquotedAttribute failed", htmlUnq, sw.toString() );

        String htmlContentUTF8 = "青森市1-2-3-4-5-６－７－８－９t 岩手県 ☺234-56789";
        String result = secureEncoder.encodeHtmlContent( htmlContentUTF8 );
        assertEquals( "encodeHTMLContentUTF8 failed", htmlContentUTF8,  result);

    }

    @Test
    public void XMLTest()
    {
        String xmlTest =
            "<!--this! is/ a; comment: --><foo attribute=value>text</foo><bar attribute=\"doublevalue\">text2</bar><baz attribute='singlevalue'>)(*#$!@#?</baz>";
        StringWriter sw = new StringWriter( xmlTest.length() * 2 );

        String xmlContent =
            "&lt;&#x21;--this&#x21; is&#x2f; a; comment: --&gt;&lt;foo attribute&#x3d;value&gt;text&lt;&#x2f;foo&gt;&lt;bar attribute&#x3d;&quot;doublevalue&quot;&gt;text2&lt;&#x2f;bar&gt;&lt;baz attribute&#x3d;&apos;singlevalue&apos;&gt;)(&#x2a;&#x23;&#x24;&#x21;&#x40;&#x23;&#x3f;&lt;&#x2f;baz&gt;";
        assertEquals( "encodeXmlContent failed", xmlContent, secureEncoder.encodeXmlContent( xmlTest ) );

        secureEncoder.encodeXmlContent( xmlTest, sw );
        assertEquals( "encodeXmlContent failed", xmlContent, sw.toString() );

        sw = new StringWriter( xmlTest.length() * 2 );
        String xmlDouble =
            "&lt;&#x21;--this&#x21; is&#x2f; a; comment: --&gt;&lt;foo attribute&#x3d;value&gt;text&lt;&#x2f;foo&gt;&lt;bar attribute&#x3d;&quot;doublevalue&quot;&gt;text2&lt;&#x2f;bar&gt;&lt;baz attribute&#x3d;'singlevalue'&gt;)(&#x2a;&#x23;&#x24;&#x21;&#x40;&#x23;&#x3f;&lt;&#x2f;baz&gt;";
        assertEquals( "encodeXmlInDoubleQuoteAttribute failed", xmlDouble,
            secureEncoder.encodeXmlInDoubleQuoteAttribute( xmlTest ) );

        secureEncoder.encodeXmlInDoubleQuoteAttribute( xmlTest, sw );
        assertEquals( "encodeXmlInDoubleQuoteAttribute failed", xmlDouble, sw.toString() );

        sw = new StringWriter( xmlTest.length() * 2 );
        String xmlSingle =
            "&lt;&#x21;--this&#x21; is&#x2f; a; comment: --&gt;&lt;foo attribute&#x3d;value&gt;text&lt;&#x2f;foo&gt;&lt;bar attribute&#x3d;\"doublevalue\"&gt;text2&lt;&#x2f;bar&gt;&lt;baz attribute&#x3d;&apos;singlevalue&apos;&gt;)(&#x2a;&#x23;&#x24;&#x21;&#x40;&#x23;&#x3f;&lt;&#x2f;baz&gt;";
        assertEquals( "encodeXmlInSingleQuoteAttribute failed", xmlSingle,
            secureEncoder.encodeXmlInSingleQuoteAttribute( xmlTest ) );

        secureEncoder.encodeXmlInSingleQuoteAttribute( xmlTest, sw );
        assertEquals( "encodeXmlInSingleQuoteAttribute failed", xmlSingle, sw.toString() );

        sw = new StringWriter( xmlTest.length() * 2 );
        String xmlComment =
            "<!&#x2d;&#x2d;this! is/ a; comment: &#x2d;&#x2d;><foo attribute=value>text</foo><bar attribute=\"doublevalue\">text2</bar><baz attribute='singlevalue'>)(*#$!@#?</baz>";
        assertEquals( "encodeXmlComment failed", xmlComment, secureEncoder.encodeXmlCommentContent( xmlTest ) );

        secureEncoder.encodeXmlCommentContent( xmlTest, sw );
        assertEquals( "encodeXmlComment failed", xmlComment, sw.toString() );

        String xmlContentUTF8 = "青森市1-2-3-4-5-６－７－８－９t 岩手県 ☺234-56789";
        String result = secureEncoder.encodeXmlContent( xmlContentUTF8 );
        assertEquals( "encodeXMLContentUTF8 failed", xmlContentUTF8,  result);

    }

    @Test
    public void JavaScriptTest()
    {
        String javascriptTest =
            "console.log(\"Log Message!\");\r\n $(ajax).postMessage('foo.com');\nvar x = 123+14*82/12-6;";
        StringWriter sw = new StringWriter( javascriptTest.length() * 2 );

        String jsHTML =
            "console.log(\\x22Log Message!\\x22);\\r\\n \\x24(ajax).postMessage(\\x27foo.com\\x27);\\nvar x = 123+14*82\\/12\\-6;";
        assertEquals( "encodeJavaScriptInHTML failed", jsHTML, secureEncoder.encodeJavaScriptInHTML( javascriptTest ) );

        secureEncoder.encodeJavaScriptInHTML( javascriptTest, sw );
        assertEquals( "encodeJavaScriptInHTML failed", jsHTML, sw.toString() );

        sw = new StringWriter( javascriptTest.length() * 2 );
        String jsAttr =
            "console.log(\\x22Log Message!\\x22);\\r\\n \\x24(ajax).postMessage(\\x27foo.com\\x27);\\nvar x = 123+14*82/12-6;";
        assertEquals( "encodeJavaScriptInAttribute", jsAttr,
            secureEncoder.encodeJavaScriptInAttribute( javascriptTest ) );

        secureEncoder.encodeJavaScriptInAttribute( javascriptTest, sw );
        assertEquals( "encodeJavaScriptInAttribute failed", jsAttr, sw.toString() );

        sw = new StringWriter( javascriptTest.length() * 2 );
        String jsBlock =
            "console.log(\\\"Log Message!\\\");\\r\\n \\x24(ajax).postMessage(\\'foo.com\\');\\nvar x = 123+14*82\\/12\\-6;";
        assertEquals( "encodeJavaScriptInBlock failed", jsBlock,
            secureEncoder.encodeJavaScriptInBlock( javascriptTest ) );

        secureEncoder.encodeJavaScriptInBlock( javascriptTest, sw );
        assertEquals( "encodeJavaScriptInBlock failed", jsBlock, sw.toString() );

        sw = new StringWriter( javascriptTest.length() * 2 );
        String jsSource =
            "console.log(\\\"Log Message!\\\");\\r\\n \\x24(ajax).postMessage(\\'foo.com\\');\\nvar x = 123+14*82/12-6;";
        assertEquals( "encodeJavaScriptInSource failed", jsSource,
            secureEncoder.encodeJavaScriptInSource( javascriptTest ) );

        secureEncoder.encodeJavaScriptInSource( javascriptTest, sw );
        assertEquals( "encodeJavaScriptInSource failed", jsSource, sw.toString() );

    }

    @Test
    public void JSONTest()
    {
        String jsonValues = "\"}{\"CustomData\":[\"foo bar\"]}";
        StringWriter sw = new StringWriter( jsonValues.length() * 2 );

        String json = "\\\"\\u007d\\u007b\\\"CustomData\\\"\\u003a\\u005b\\\"foo\\u0020bar\\\"\\u005d\\u007d";
        assertEquals( "encodeJSONValue failed", json, secureEncoder.encodeJSONValue( jsonValues ) );

        secureEncoder.encodeJSONValue( jsonValues, sw );
        assertEquals( "encodeJSONValue failed", json, sw.toString() );

    }

    @Test
    public void URITest()
    {
        String URI = "?foo=bar&test=^42@314*(&SF&Ts=+~\u0732";
        StringWriter sw = new StringWriter( URI.length() * 2 );

        String uriComp = "%3Ffoo%3Dbar%26test%3D%5E42%40314*(%26SF%26Ts%3D%2B~%DC%B2";
        assertEquals( "encodeUriComponent failed", uriComp, secureEncoder.encodeUriComponent( URI ) );

        secureEncoder.encodeUriComponent( URI, sw );
        assertEquals( "encodeUriComponent failed", uriComp, sw.toString() );

        sw = new StringWriter( URI.length() * 2 );
        String uriStrict = "%3Ffoo%3Dbar%26test%3D%5E42%40314*%28%26SF%26Ts%3D%2B~%DC%B2";
        assertEquals( "encodeUriComponentStrict failed", uriStrict, secureEncoder.encodeUriComponentStrict( URI ) );

        secureEncoder.encodeUriComponentStrict( URI, sw );
        assertEquals( "encodeUriComponentStrict failed", uriStrict, sw.toString() );

    }

}
