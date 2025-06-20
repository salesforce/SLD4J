/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.validation.encoders;

import java.io.IOException;
import java.io.Writer;

import com.salesforce.sld.validation.encoders.impl.CDATAManipulator;
import com.salesforce.sld.validation.encoders.impl.HTMLManipulator;
import com.salesforce.sld.validation.encoders.impl.JSONManipulator;
import com.salesforce.sld.validation.encoders.impl.JavaScriptManipulator;
import com.salesforce.sld.validation.encoders.impl.URIManipulator;
import com.salesforce.sld.validation.encoders.impl.XMLManipulator;
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

/**
 * SecureEncode contains many methods for manipulating untrusted data Strings into RFC-Compliant Strings for a given
 * context by encoding "bad" data into the proper format.
 */
public class SecureEncoder
{
    // Default HTML Manipulators
    private static HTMLManipulator htmlContentManipulator = new HTMLContentManipulator();

    private static HTMLManipulator htmlSingleQuoteManipulator = new HTMLSingleQuotedAttrManipulator();

    private static HTMLManipulator htmlDoubleQuoteManipulator = new HTMLDoubleQuotedAttrManipulator();

    private static HTMLManipulator htmlUnQuoteManipulator = new HTMLUnquotedAttrManipulator();

    // Default JS Manipulators
    private static JavaScriptManipulator jsHtmlManipulator = new JavaScriptHTMLManipulator();

    private static JavaScriptManipulator jsAttrManipulator = new JavaScriptAttrManipulator();

    private static JavaScriptManipulator jsBlockManipulator = new JavaScriptBlockManipulator();

    private static JavaScriptManipulator jsSrcManipulator = new JavaScriptSourceManipulator();

    // Default JSON Manipulators
    private static JSONManipulator jsonValueManipulator = new JSONValueManipulator();

    // Default URI Manipulators
    private static URIManipulator uriLenientManipulator = new URILenientManipulator();

    private static URIManipulator uriStrictManipulator = new URIStrictManipulator();

    // Default XML Manipulators
    private static XMLManipulator xmlContentManipulator = new XMLContentManipulator();

    private static XMLManipulator xmlSingleQuoteManipulator = new XMLSingleQuotedAttrManipulator();

    private static XMLManipulator xmlDoubleQuoteManipulator = new XMLDoubleQuotedAttrManipulator();

    private static XMLManipulator xmlCommentManipulator = new XMLCommentContentManipulator();

    /**
     * Shared method to handle encoder lookup by type and dispatch string
     * 
     * @param manipulator the manipulator to use 
     * @param input the string to encode
     * @return a properly encoded string representation of the input string, or null if the input is null
     */
    protected String encode( AbstractManipulator manipulator, String input )
    {
        return manipulator.encode( input );
    }

    /**
     * Shared method to handle encoder lookup by type and dispatch string to be written with the given writer
     * 
     * @param manipulator the manipulator to use 
     * @param input the string to encode.
     * @param writer a Writer to write output to
     */
    protected void encode( AbstractManipulator manipulator, String input, Writer writer )
    {
        try
        {
            manipulator.encode( input, writer );
        }
        catch ( IOException e )
        {
            throw new IllegalArgumentException( "An error occurred while encoding", e );
        }
    }

    /**
     * <p>
     * Encodes content within a CDATA element.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * String cdata = "&lt;![CDATA[" + SecureEncode.encodeCDATAContent( untrustedInput ) + "]]&gt;";
     * </pre>
     * 
     * <b> Flow: </b>
     * <ul>
     * <li>Allow all AlphaNumerics, Special characters and Unicode</li>
     * <li>Disallow Control Characters</li>
     * <li>Replace instances of ]]&gt; with ]]&gt;]]&lt;![CDATA[&gt;</li>
     * </ul>
     * 
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeCDATAContent( String input )
    {
        return encode( new CDATAManipulator(), input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeCDATAContent(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeCDATAContent( String input, Writer out )
    {
        encode( new CDATAManipulator(), input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in a general HTML context. E.g. text content and text attributes. This method takes
     * the UNION of allowed characters among all contexts, so may be more imprecise than the more specific contexts.
     * Generally, this method is preferred unless you specifically understand the context in which untrusted data will
     * be displayed.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;div&gt;${SecureEncode.encodeHtmlContent(unsafeData)}&lt;/div&gt;
     *
     * &lt;input value="${SecureEncode.encodeHtmlContent(unsafeData)}" /&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Replace Illegal Control Characters (Below 0x1F or between 0x7F and 0x9F) with &amp;#xfffd;, the Unicode
     * Replacement Character</li>
     * <li>Replace special HTML characters with their HTML Entity equivalents</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeHtmlContent( String input )
    {
        return encode( htmlContentManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeHtmlContent(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeHtmlContent( String input, Writer out )
    {
        encode( htmlContentManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in an HTML Attribute guarded by a single quote. This method is preferred if you
     * understand exactly how the output of this will be used in the HTML document.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;div id='${SecureEncode.encodeHtmlInSingleQuoteAttribute(unsafeData)}'&gt;&lt;/div&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Replace Illegal Control Characters (Below 0x1F or between 0x7F and 0x9F) with &amp;#xfffd;, the Unicode
     * Replacement Character</li>
     * <li>Replace special HTML characters with their HTML Entity equivalents</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeHtmlInSingleQuoteAttribute( String input )
    {
        return encode( htmlSingleQuoteManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeHtmlInSingleQuoteAttribute(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeHtmlInSingleQuoteAttribute( String input, Writer out )
    {
        encode( htmlSingleQuoteManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in an HTML Attribute guarded by a double quote. This method is preferred if you
     * understand exactly how the output of this will be used in the HTML document.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;div id="${SecureEncode.encodeHtmlInDoubleQuoteAttribute(unsafeData)}"&gt;&lt;/div&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Replace Illegal Control Characters (Below 0x1F or between 0x7F and 0x9F) with &amp;#xfffd;, the Unicode
     * Replacement Character</li>
     * <li>Replace special HTML characters with their HTML Entity equivalents</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeHtmlInDoubleQuoteAttribute( String input )
    {
        return encode( htmlDoubleQuoteManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeHtmlInDoubleQuoteAttribute(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeHtmlInDoubleQuoteAttribute( String input, Writer out )
    {
        encode( htmlDoubleQuoteManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in an HTML Attribute left unguarded. This method is preferred if you understand
     * exactly how the output of this will be used in the HTML document.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;div id=${SecureEncode.encodeHtmlUnquotedAttribute(unsafeData)}&gt;&lt;/div&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Replace Illegal Control Characters (Below 0x1F or between 0x7F and 0x9F) with &amp;#xfffd;, the Unicode
     * Replacement Character</li>
     * <li>Replace special HTML characters with their HTML Entity equivalents</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeHtmlUnquotedAttribute( String input )
    {
        return encode( htmlUnQuoteManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeHtmlUnquotedAttribute(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeHtmlUnquotedAttribute( String input, Writer out )
    {
        encode( htmlUnQuoteManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in JavaScript inside an HTML context. This method takes the UNION of allowed
     * characters among the other contexts, so may be more imprecise than the more specific contexts. Generally, this
     * method is preferred unless you specifically understand the context in which untrusted data will be displayed.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;script type="text/javascript"&gt;
     *     var data = "${SecureEncode.encodeJavaScriptInHTML(unsafeData)}";
     * &lt;/script&gt;
     *
     * &lt;button onclick="alert('${SecureEncode.encodeJavaScriptInHTML(unsafeData)}');"&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Slash escape certain illegal characters</li>
     * <li>Replace special JavaScript characters with their Hex Encoded equivalents prepended with \\x for character
     * codes under 128 and \\u for character codes over 128</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeJavaScriptInHTML( String input )
    {
        return encode( jsHtmlManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeJavaScriptInHTML(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeJavaScriptInHTML( String input, Writer out )
    {
        encode( jsHtmlManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in JavaScript inside an HTML attribute. This method is preferred if you understand
     * exactly how the output of this will be used in the page
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;button onclick="alert('${SecureEncode.encodeJavaScriptInAttribute(unsafeData)}');"&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Slash escape certain illegal characters</li>
     * <li>Replace special JavaScript characters with their Hex Encoded equivalents prepended with \\x for character
     * codes under 128 and \\u for character codes over 128</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeJavaScriptInAttribute( String input )
    {
        return encode( jsAttrManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeJavaScriptInAttribute(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeJavaScriptInAttribute( String input, Writer out )
    {
        encode( jsAttrManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in JavaScript inside an HTML block. This method is preferred if you understand
     * exactly how the output of this will be used in the page
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;script type="text/javascript"&gt;
     *     var data = "${SecureEncode.encodeJavaScriptInBlock(unsafeData)}";
     * &lt;/script&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Slash escape certain illegal characters</li>
     * <li>Replace special JavaScript characters with their Hex Encoded equivalents prepended with \\x for character
     * codes under 128 and \\u for character codes over 128</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeJavaScriptInBlock( String input )
    {
        return encode( jsBlockManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeJavaScriptInBlock(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeJavaScriptInBlock( String input, Writer out )
    {
        encode( jsBlockManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in JavaScript inside a JavaScript source file. This method is preferred if you
     * understand exactly how the output of this will be used in the page
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;...inside foobar.js...&gt;
     * var data = "${SecureEncode.encodeJavaScriptInSource(unsafeData)}";
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Slash escape certain illegal characters</li>
     * <li>Replace special JavaScript characters with their Hex Encoded equivalents prepended with \\x for character
     * codes under 128 and \\u for character codes over 128</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeJavaScriptInSource( String input )
    {
        return encode( jsSrcManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeJavaScriptInSource(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeJavaScriptInSource( String input, Writer out )
    {
        encode( jsSrcManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in a JSON Object Value to prevent escaping into a trusted context.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * var json = {"trusted_data" : SecureEncoder.encodeJSONValue(unsafeData)};
     * return JSON.stringify(json);
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics</li>
     * <li>Slash escape certain illegal characters</li>
     * <li>Replace all other characters with their Hex Encoded equivalents prepended with \\u</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeJSONValue( String input )
    {
        return encode( jsonValueManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeJSONValue(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeJSONValue( String input, Writer out )
    {
        encode( jsonValueManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use as a component of a URI. This is equivalent to javascript's encodeURIComponent and
     * does a realistic job of encoding. It outputs UTF-8(hex) encoded data. For instance, encoding char \u0732 will
     * result in %DC%B2 as output, as opposed to the %732 UTF-16(hex).
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;a href="http://host.com?value=${SecureEncoder.encodeUriComponent(unsafeData)}"/&gt;
     * </pre>
     *
     * <b>Allows:</b>
     * 
     * <pre>
     * A-Z, a-z, 0-9, -, _, ., ~, !, *, ', (, )
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Percent encode all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeUriComponent( String input )
    {
        return encode( uriLenientManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeUriComponent(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeUriComponent( String input, Writer out )
    {
        encode( uriLenientManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use as a component of a URI. This is a strict encoder and fully complies with RFC3986.
     * It outputs UTF-8(hex) encoded data. For instance, encoding char \u0732 will result in %DC%B2 as output, as
     * opposed to the %732 UTF-16(hex).
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;a href="http://host.com?value=${SecureEncoder.encodeUriComponentStrict(unsafeData)}"/&gt;
     * </pre>
     *
     * <b>Allows:</b>
     * 
     * <pre>
     * A-Z, a-z, 0-9, -, _, ., ~
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Percent encode all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeUriComponentStrict( String input )
    {
        return encode( uriStrictManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeUriComponentStrict(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeUriComponentStrict( String input, Writer out )
    {
        encode( uriStrictManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in a general XML context. E.g. text content and text attributes. This method takes
     * the UNION of allowed characters between the other contexts, so may be more imprecise than the more specific
     * contexts. Generally, this method is preferred unless you specifically understand the context in which untrusted
     * data will be displayed.
     * </p>
     * <b>Note: It is recommended that you use a real parser, as this method can be misused, but is left here if a
     * parser is unavailable to you</b> <br>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;foo&gt;${SecureEncode.encodeXmlContent(unsafeData)}&lt;/foo&gt;
     *
     * &lt;bar attr="${SecureEncode.encodeXmlContent(unsafeData)}"&gt;&lt;/bar&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Replace Illegal Control Characters (Below 0x1F or between 0x7F and 0x84 or between 0x86 and 0x9F or between
     * 0xFDD0 and 0xFDDF) with an empty string</li>
     * <li>Replace special XML characters with their default XML Entity equivalents</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeXmlContent( String input )
    {
        return encode( xmlContentManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeXmlContent(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeXmlContent( String input, Writer out )
    {
        encode( xmlContentManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in an XML attribute guarded by a single quote. This method is preferred if you
     * understand the context in which untrusted data will be displayed.
     * </p>
     * <b>Note: It is recommended that you use a real parser, as this method can be misused, but is left here if a
     * parser is unavailable to you</b> <br>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;bar attr='${SecureEncode.encodeXmlInSingleQuoteAttribute(unsafeData)}'&gt;&lt;/bar&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Replace Illegal Control Characters (Below 0x1F or between 0x7F and 0x84 or between 0x86 and 0x9F or between
     * 0xFDD0 and 0xFDDF) with an empty string</li>
     * <li>Replace special XML characters with their default XML Entity equivalents</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeXmlInSingleQuoteAttribute( String input )
    {
        return encode( xmlSingleQuoteManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeXmlInSingleQuoteAttribute(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeXmlInSingleQuoteAttribute( String input, Writer out )
    {
        encode( xmlSingleQuoteManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in an XML attribute guarded by a double quote. This method is preferred if you
     * understand the context in which untrusted data will be displayed.
     * </p>
     * <b>Note: It is recommended that you use a real parser, as this method can be misused, but is left here if a
     * parser is unavailable to you</b> <br>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;bar attr="${SecureEncode.encodeXmlInDoubleQuoteAttribute(unsafeData)}"&gt;&lt;/bar&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Replace Illegal Control Characters (Below 0x1F or between 0x7F and 0x84 or between 0x86 and 0x9F or between
     * 0xFDD0 and 0xFDDF) with an empty string</li>
     * <li>Replace special XML characters with their default XML Entity equivalents</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeXmlInDoubleQuoteAttribute( String input )
    {
        return encode( xmlDoubleQuoteManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeXmlInDoubleQuoteAttribute(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeXmlInDoubleQuoteAttribute( String input, Writer out )
    {
        encode( xmlDoubleQuoteManipulator, input, out );
    }

    /**
     * <p>
     * Encodes a given input for use in an XML comments. This method is preferred if you understand the context in which
     * untrusted data will be displayed.
     * </p>
     * <b>Note: It is recommended that you use a real parser, as this method can be misused, but is left here if a
     * parser is unavailable to you</b> <br>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;!-- ${SecureEncoder.encodeXmlCommentContent(unsafeData)} --&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Replace Illegal Control Characters (Below 0x1F or between 0x7F and 0x84 or between 0x86 and 0x9F or between
     * 0xFDD0 and 0xFDDF) with an empty string</li>
     * <li>Replace special XML characters with their default XML Entity equivalents</li>
     * </ul>
     *
     * @param input untrusted input to be encoded, if necessary
     * @return a properly encoded string for the given input, or null if the input is null
     */
    public String encodeXmlCommentContent( String input )
    {
        return encode( xmlCommentManipulator, input );
    }

    /**
     * Writes encoded content directly to given java.io.Writer See {@link #encodeXmlCommentContent(String)}
     *
     * @param input untrusted input to be encoded, if necessary
     * @param out where to write the encoded output
     */
    public void encodeXmlCommentContent( String input, Writer out )
    {
        encode( xmlCommentManipulator, input, out );
    }

}
