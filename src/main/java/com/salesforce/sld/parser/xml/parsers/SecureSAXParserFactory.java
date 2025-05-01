/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.parser.xml.parsers;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import com.salesforce.sld.parser.xml.sax.SecureXMLReader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Secured SAXParserFactory wrapper. It implements a set of features on the
 * wrapped factory that enforce best security practices. This wrapper disables
 * the ability to resolve external dtds and entities. These features cannot be
 * overwritten
 * <p>
 * During parsing, the created SAXParser will report a SAXParseException if a
 * file attempts a DOS attack, and will skip any illegal calls in the document
 * 
 * @author csmith
 */
public class SecureSAXParserFactory
    extends SAXParserFactory
{
    private static Map<String, Boolean> FEATURE_MAP = setupFeatureMap();

    private final SAXParserFactory factory;

    /**
     * Create a map with a set of features to be used by all wrapped factories
     * that enforce best security practices. This map disables the ability to
     * resolve external dtds and entities. These features cannot be changed.
     * See original OWASP guideline for futher reference:
     * https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#jaxp-documentbuilderfactory-saxparserfactory-and-dom4j
     * @return Collections.unmodifiableMap( features )
     */
    private static Map<String, Boolean> setupFeatureMap( ) {
        Map<String, Boolean> features = new HashMap<String, Boolean>();

        // If you can't completely disable DTDs, then at least do the following:
        // Xerces 1 -
        // http://xerces.apache.org/xerces-j/features.html#external-general-entities
        // Xerces 2 -
        // http://xerces.apache.org/xerces2-j/features.html#external-general-entities
        // JDK7+ - http://xml.org/sax/features/external-general-entities
        features.put("http://xml.org/sax/features/external-general-entities", false);

        // Xerces 1 -
        // http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
        // Xerces 2 -
        // http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
        // JDK7+ - http://xml.org/sax/features/external-parameter-entities
        features.put("http://xml.org/sax/features/external-parameter-entities", false);

        // Disable external DTDs as well
        features.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        return Collections.unmodifiableMap( features );
    }

    /**
     * <p>Obtain a new instance of a <code>SAXParserFactory</code>.
     * This method creates a new factory instance.</p>
     * @param factoryClassName fully qualified factory class name that provides implementation of SAXParserFactory.
     *
     * @param classLoader <code>ClassLoader</code> used to load the factory class. If <code>null</code>
     *                     current <code>Thread</code>'s context classLoader is used to load the factory class.
     * @return new SAXParserFactory instance.
     */
    private SAXParserFactory newSecureSAXParserFactory( String factoryClassName, ClassLoader classLoader )
    {
        SAXParserFactory spf;
        if ( factoryClassName != null )
        {
            spf = SAXParserFactory.newInstance( factoryClassName, classLoader );
        }
        else
        {
            spf = SAXParserFactory.newInstance();
        }

        for ( Map.Entry<String, Boolean> entry : FEATURE_MAP.entrySet() )
        {
            try
            {
                spf.setFeature( entry.getKey(), entry.getValue() );
            }
            catch ( Exception e )
            {
                // Allow these exceptions as it means the security features
                // can't be mis-set
            }
        }

        return spf;
    }

    protected SecureSAXParserFactory( String factoryClassName, ClassLoader classLoader )
    {
        this.factory = newSecureSAXParserFactory(factoryClassName, classLoader);
    }

    /**
     * Create a new instance of the {@linkplain SecureSAXParserFactory}
     * 
     * @see SAXParserFactory#newInstance()
     * @return New instance of a {@linkplain SecureSAXParserFactory}
     */
    public static SecureSAXParserFactory newInstance()
    {
        return new SecureSAXParserFactory( null, null );
    }

    /**
     * Create a new instance of the {@linkplain SecureSAXParserFactory} using
     * the provided classname
     * <p>
     * if the classname is null, the default fallback factory is loaded instead
     *
     * @see SAXParserFactory#newInstance(String, ClassLoader)
     * @param factoryClassName fully qualified factory class name that provides
     *            implementation of
     *            {@linkplain javax.xml.parsers.SAXParserFactory}.
     * @param classLoader {@linkplain ClassLoader} used to load the factory
     *            class. If <code>null</code> current {@linkplain Thread}'s
     *            context classLoader is used to load the factory class.
     * @return a new instance of {@linkplain SecureSAXParserFactory}
     */
    public static SecureSAXParserFactory newInstance( String factoryClassName, ClassLoader classLoader )
    {
        return new SecureSAXParserFactory( factoryClassName, classLoader );
    }

    @Override
    public SAXParser newSAXParser()
        throws ParserConfigurationException, SAXException
    {
        return this.factory.newSAXParser();
    }

    /**
     * Like {@linkplain #newSAXParser()}, create and return a
     * {@linkplain SAXParser} object, however, also configure the underlying
     * {@linkplain XMLReader} to be a {@linkplain SecureXMLReader} and disallow
     * all DOCTYPES
     * 
     * @return a {@linkplain SAXParser} with a configured
     *         {@linkplain SecureXMLReader}
     * @throws ParserConfigurationException - if a parser cannot be created
     *             which satisfies the requested configuration.
     * @throws SAXException - for SAX errors.
     */
    public SAXParser newStrictSAXParser()
        throws ParserConfigurationException, SAXException
    {
        SAXParser parser = this.factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setFeature( "http://apache.org/xml/features/disallow-doctype-decl", true );
        reader = new SecureXMLReader( reader );
        return parser;
    }

    @Override
    public void setFeature( String name, boolean value )
        throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
    {
        // don't penalize someone for doing the right thing
        if ( FEATURE_MAP.containsKey( name ) && FEATURE_MAP.get( name ) != value )
        {
            throw new SAXNotSupportedException( "Cannot overwrite security feature: " + name );
        }
        this.factory.setFeature( name, value );
    }

    @Override
    public boolean getFeature( String name )
        throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
    {
        return this.factory.getFeature( name );
    }

    @Override
    public void setXIncludeAware( final boolean state )
    {
        this.factory.setXIncludeAware( state );
    }

    @Override
    public boolean isXIncludeAware()
    {
        return this.factory.isXIncludeAware();
    }

    @Override
    public void setSchema( Schema schema )
    {
        this.factory.setSchema( schema );
    }

    @Override
    public Schema getSchema()
    {
        return this.factory.getSchema();
    }

    @Override
    public void setNamespaceAware( boolean awareness )
    {
        this.factory.setNamespaceAware( awareness );
    }

    @Override
    public void setValidating( boolean validating )
    {
        this.factory.setValidating( validating );
    }

    @Override
    public boolean isNamespaceAware()
    {
        return this.factory.isNamespaceAware();
    }

    @Override
    public boolean isValidating()
    {
        return this.factory.isValidating();
    }
}
