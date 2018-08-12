/*
 * Copyright (c) 2018, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.encoders;

import java.io.IOException;
import java.io.Writer;

import com.salesforce.sld.encoders.impl.CDATAManipulator;
import com.salesforce.sld.encoders.impl.JSONManipulator;
import com.salesforce.sld.encoders.impl.URIManipulator;
import com.salesforce.sld.encoders.impl.XMLManipulator;
import com.salesforce.sld.encoders.impl.html.HTMLContentManipulator;
import com.salesforce.sld.encoders.impl.html.HTMLDoubleQuotedAttrManipulator;
import com.salesforce.sld.encoders.impl.html.HTMLSingleQuotedAttrManipulator;
import com.salesforce.sld.encoders.impl.html.HTMLUnquotedAttrManipulator;
import com.salesforce.sld.encoders.impl.js.JavaScriptAttrManipulator;
import com.salesforce.sld.encoders.impl.js.JavaScriptBlockManipulator;
import com.salesforce.sld.encoders.impl.js.JavaScriptHTMLManipulator;
import com.salesforce.sld.encoders.impl.js.JavaScriptSourceManipulator;
import com.salesforce.sld.encoders.impl.json.JSONValueManipulator;
import com.salesforce.sld.encoders.impl.uri.URILenientManipulator;
import com.salesforce.sld.encoders.impl.uri.URIStrictManipulator;
import com.salesforce.sld.encoders.impl.xml.XMLCommentContentManipulator;
import com.salesforce.sld.encoders.impl.xml.XMLContentManipulator;
import com.salesforce.sld.encoders.impl.xml.XMLDoubleQuotedAttrManipulator;
import com.salesforce.sld.encoders.impl.xml.XMLSingleQuotedAttrManipulator;

/**
 * SecureFilter contains many methods for manipulating untrusted data Strings into RFC-Compliant Strings for a given
 * context by removing "bad" data from the untrusted data.
 */
public class SecureFilter
{
    // Default HTML Manipulators
    private static HTMLContentManipulator htmlContentManipulator = new HTMLContentManipulator();

    private static HTMLSingleQuotedAttrManipulator htmlSingleQuoteManipulator = new HTMLSingleQuotedAttrManipulator();

    private static HTMLDoubleQuotedAttrManipulator htmlDoubleQuoteManipulator = new HTMLDoubleQuotedAttrManipulator();

    private static HTMLUnquotedAttrManipulator htmlUnQuoteManipulator = new HTMLUnquotedAttrManipulator();

    // Default JS Manipulators
    private static JavaScriptHTMLManipulator jsHtmlManipulator = new JavaScriptHTMLManipulator();

    private static JavaScriptAttrManipulator jsAttrManipulator = new JavaScriptAttrManipulator();

    private static JavaScriptBlockManipulator jsBlockManipulator = new JavaScriptBlockManipulator();

    private static JavaScriptSourceManipulator jsSrcManipulator = new JavaScriptSourceManipulator();

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
     * Shared method to handle filter lookup by type and dispatch string
     * 
     * @param manipulator the manipulator to use 
     * @param input the string to filter
     * @return a properly encoded string representation of the input string
     */
    protected String filter( AbstractManipulator manipulator, String input )
    {
        return manipulator.filter( input );
    }

    /**
     * Shared method to handle filter lookup by type and dispatch string to be written with the given writer
     * 
     * @param manipulator the manipulator to use 
     * @param input the string to filter
     * @param writer a Writer to write output to
     */
    protected void filter( AbstractManipulator manipulator, String input, Writer writer )
    {
        try
        {
            manipulator.filter( input, writer );
        }
        catch ( IOException e )
        {
            throw new IllegalArgumentException( "An error occurred while filtering", e );
        }
    }

    /**
     * <p>
     * Filters content within a CDATA element.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * String cdata = "&lt;![CDATA[" + SecureFilter.filterCDATAContent( untrustedInput ) + "]]&gt;";
     * </pre>
     * 
     * <b> Flow: </b>
     * <ul>
     * <li>Allow all AlphaNumerics, Special characters and Unicode</li>
     * <li>Disallow Control Characters</li>
     * <li>Remove instances of ]]&gt;</li>
     * </ul>
     * 
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterCDATAContent( String input )
    {
        return filter( new CDATAManipulator(), input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterCDATAContent(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterCDATAContent( String input, Writer out )
    {
        filter( new CDATAManipulator(), input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in a general HTML context. E.g. text content and text
     * attributes. This method takes the UNION of allowed characters among all contexts, so may be more imprecise than
     * the more specific contexts. Generally, this method is preferred unless you specifically understand the context in
     * which untrusted data will be displayed.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;div&gt;${SecureFilter.filterHtmlContent(unsafeData)}&lt;/div&gt;
     *
     * &lt;input value="${SecureFilter.filterHtmlContent(unsafeData)}" /&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterHtmlContent( String input )
    {
        return filter( htmlContentManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterHtmlContent(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterHtmlContent( String input, Writer out )
    {
        filter( htmlContentManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in an HTML Attribute guarded by a single quote. This method
     * is preferred if you understand exactly how the output of this will be used in the HTML document.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;div id='${SecureFilter.filterHtmlInSingleQuoteAttribute(unsafeData)}'&gt;&lt;/div&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterHtmlInSingleQuoteAttribute( String input )
    {
        return filter( htmlSingleQuoteManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterHtmlInSingleQuoteAttribute(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterHtmlInSingleQuoteAttribute( String input, Writer out )
    {
        filter( htmlSingleQuoteManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in an HTML Attribute guarded by a double quote. This method
     * is preferred if you understand exactly how the output of this will be used in the HTML document.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;div id="${SecureFilter.filterHtmlInDoubleQuoteAttribute(unsafeData)}"&gt;&lt;/div&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterHtmlInDoubleQuoteAttribute( String input )
    {
        return filter( htmlDoubleQuoteManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterHtmlInDoubleQuoteAttribute(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterHtmlInDoubleQuoteAttribute( String input, Writer out )
    {
        filter( htmlDoubleQuoteManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in an HTML Attribute left unguarded. This method is
     * preferred if you understand exactly how the output of this will be used in the HTML document.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;div id=${SecureFilter.filterHtmlUnquotedAttribute(unsafeData)}&gt;&lt;/div&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterHtmlUnquotedAttribute( String input )
    {
        return filter( htmlUnQuoteManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterHtmlUnquotedAttribute(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output, or null if the input is null
     */
    public void filterHtmlUnquotedAttribute( String input, Writer out )
    {
        filter( htmlUnQuoteManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in JavaScript inside an HTML context. This method takes the
     * UNION of allowed characters among the other contexts, so may be more imprecise than the more specific contexts.
     * Generally, this method is preferred unless you specifically understand the context in which untrusted data will
     * be displayed.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;script type="text/javascript"&gt;
     *     var data = "${SecureFilter.filterJavaScriptInHTML(unsafeData)}";
     * &lt;/script&gt;
     *
     * &lt;button onclick="alert('${SecureFilter.filterJavaScriptInHTML(unsafeData)}');"&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterJavaScriptInHTML( String input )
    {
        return filter( jsHtmlManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterJavaScriptInHTML(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterJavaScriptInHTML( String input, Writer out )
    {
        filter( jsHtmlManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in JavaScript inside an HTML attribute. This method is
     * preferred if you understand exactly how the output of this will be used in the page
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;button onclick="alert('${SecureFilter.filterJavaScriptInAttribute(unsafeData)}');"&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterJavaScriptInAttribute( String input )
    {
        return filter( jsAttrManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterJavaScriptInAttribute(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterJavaScriptInAttribute( String input, Writer out )
    {
        filter( jsAttrManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in JavaScript inside an HTML block. This method is
     * preferred if you understand exactly how the output of this will be used in the page
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;script type="text/javascript"&gt;
     *     var data = "${SecureFilter.filterJavaScriptInBlock(unsafeData)}";
     * &lt;/script&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterJavaScriptInBlock( String input )
    {
        return filter( jsBlockManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterJavaScriptInBlock(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterJavaScriptInBlock( String input, Writer out )
    {
        filter( jsBlockManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in JavaScript inside a JavaScript source file. This method
     * is preferred if you understand exactly how the output of this will be used in the page
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;...inside foobar.js...&gt;
     * var data = "${SecureFilter.filterJavaScriptInSource(unsafeData)}";
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterJavaScriptInSource( String input )
    {
        return filter( jsSrcManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterJavaScriptInSource(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterJavaScriptInSource( String input, Writer out )
    {
        filter( jsSrcManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in a JSON Object Value to prevent escaping into a trusted
     * context.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * var json = {"trusted_data" : SecureFilter.filterJSONValue(unsafeData)};
     * return JSON.stringify(json);
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterJSONValue( String input )
    {
        return filter( jsonValueManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterJSONValue(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterJSONValue( String input, Writer out )
    {
        filter( jsonValueManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use as a component of a URI. This is equivalent to javascript's
     * filterURIComponent and does a realistic job of encoding.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;a href="http://host.com?value=${SecureFilter.filterUriComponent(unsafeData)}"/&gt;
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
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterUriComponent( String input )
    {
        return filter( uriLenientManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterUriComponent(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterUriComponent( String input, Writer out )
    {
        filter( uriLenientManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use as a component of a URI. This is a strict filter and fully
     * complies with RFC3986.
     * </p>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;a href="http://host.com?value=${SecureFilter.filterUriComponentStrict(unsafeData)}"/&gt;
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
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterUriComponentStrict( String input )
    {
        return filter( uriStrictManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterUriComponentStrict(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterUriComponentStrict( String input, Writer out )
    {
        filter( uriStrictManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in a general XML context. E.g. text content and text
     * attributes. This method takes the UNION of allowed characters between the other contexts, so may be more
     * imprecise than the more specific contexts. Generally, this method is preferred unless you specifically understand
     * the context in which untrusted data will be displayed.
     * </p>
     * <b>Note: It is recommended that you use a real parser, as this method can be misused, but is left here if a
     * parser is unavailable to you</b> <br>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;foo&gt;${SecureFilter.filterXmlContent(unsafeData)}&lt;/foo&gt;
     *
     * &lt;bar attr="${SecureFilter.filterXmlContent(unsafeData)}"&gt;&lt;/bar&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterXmlContent( String input )
    {
        return filter( xmlContentManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterXmlContent(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterXmlContent( String input, Writer out )
    {
        filter( xmlContentManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in an XML attribute guarded by a single quote. This method
     * is preferred if you understand the context in which untrusted data will be displayed.
     * </p>
     * <b>Note: It is recommended that you use a real parser, as this method can be misused, but is left here if a
     * parser is unavailable to you</b> <br>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;bar attr='${SecureFilter.filterXmlInSingleQuoteAttribute(unsafeData)}'&gt;&lt;/bar&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterXmlInSingleQuoteAttribute( String input )
    {
        return filter( xmlSingleQuoteManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterXmlInSingleQuoteAttribute(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterXmlInSingleQuoteAttribute( String input, Writer out )
    {
        filter( xmlSingleQuoteManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in an XML attribute guarded by a double quote. This method
     * is preferred if you understand the context in which untrusted data will be displayed.
     * </p>
     * <b>Note: It is recommended that you use a real parser, as this method can be misused, but is left here if a
     * parser is unavailable to you</b> <br>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;bar attr="${SecureFilter.filterXmlInDoubleQuoteAttribute(unsafeData)}"&gt;&lt;/bar&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterXmlInDoubleQuoteAttribute( String input )
    {
        return filter( xmlDoubleQuoteManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterXmlInDoubleQuoteAttribute(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterXmlInDoubleQuoteAttribute( String input, Writer out )
    {
        filter( xmlDoubleQuoteManipulator, input, out );
    }

    /**
     * <p>
     * Filters illegal characters from a given input for use in an XML comments. This method is preferred if you
     * understand the context in which untrusted data will be displayed.
     * </p>
     * <b>Note: It is recommended that you use a real parser, as this method can be misused, but is left here if a
     * parser is unavailable to you</b> <br>
     * <b>Example Usage:</b>
     * 
     * <pre>
     * &lt;!-- ${SecureFilter.filterXmlCommentContent(unsafeData)} --&gt;
     * </pre>
     *
     * <b>Flow:</b>
     * <ul>
     * <li>Allow AlphaNumerics and some Special characters</li>
     * <li>Remove all other characters</li>
     * </ul>
     *
     * @param input untrusted input to be filtered, if necessary
     * @return a properly filtered string for the given input, or null if the input is null
     */
    public String filterXmlCommentContent( String input )
    {
        return filter( xmlCommentManipulator, input );
    }

    /**
     * Writes filtered content directly to given java.io.Writer See {@link #filterXmlCommentContent(String)}
     *
     * @param input untrusted input to be filtered, if necessary
     * @param out where to write the filtered output
     */
    public void filterXmlCommentContent( String input, Writer out )
    {
        filter( xmlCommentManipulator, input, out );
    }

}
