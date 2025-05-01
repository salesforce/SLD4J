/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.parser.xml.transform.sax;


import javax.xml.XMLConstants;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.XMLFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Secured SAXTransformerFactory wrapper. It implements a set of attributes on
 * the wrapped factory that enforce best security practices. This wrapper
 * disables the ability to access external DTDs and schemas. These attributes
 * cannot be overwritten
 * <p>
 * During parsing, the created Transformer will report a TransformerException if
 * a schema attempts an illegal operation
 * 
 * @author csmith
 */
public class SecureSAXTransformerFactory
    extends SAXTransformerFactory
{
    private static Map<String, String> ATTRIBUTE_MAP = setupAttributeMap();

    private final SAXTransformerFactory factory;

    /**
     * Create a map with a set of attributes to be used by all wrapped factories
     * that enforce best security practices. This map disables the ability to
     * resolve external dtds and entities. These attributes cannot be changed.
     * See original OWASP guideline for futher reference:
     * https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#jaxp-documentbuilderfactory-saxparserfactory-and-dom4j
     * @return Collections.unmodifiableMap( features )
     */
    private static Map<String, String> setupAttributeMap( )
    {
        Map<String, String> attributes = new HashMap<String, String>();

        attributes.put(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        attributes.put(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        return Collections.unmodifiableMap( attributes );
    }

    /**
     * <p>Obtain a new instance of a <code>SAXTransformerFactory</code>.
     * This method creates a new factory instance.</p>
     * @param factoryClassName fully qualified factory class name that provides implementation of SAXTransformerFactory.
     *
     * @param classloader <code>ClassLoader</code> used to load the factory class. If <code>null</code>
     *                     current <code>Thread</code>'s context classLoader is used to load the factory class.
     * @return new SAXTransformerFactory instance.
     *
     * @exception java.lang.IllegalArgumentException If the attribute value
     *            is attempted to be changed from the default defined on
     *            ATTRIBUTE_MAP.
     */
    private SAXTransformerFactory newSecureSAXTransformerFactory( String factoryClassName, ClassLoader classloader )
    {
        SAXTransformerFactory stf;
        if ( factoryClassName != null )
        {
            stf = (SAXTransformerFactory) SAXTransformerFactory.newInstance( factoryClassName, classloader );
        }
        else
        {
            stf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        }

        for ( Map.Entry<String, String> entry : ATTRIBUTE_MAP.entrySet() )
        {
            try
            {
                stf.setAttribute( entry.getKey(), entry.getValue() );
            }
            catch ( IllegalArgumentException e )
            {
                // Allow these exceptions as it means the security features
                // can't be mis-set
            }
        }

        return stf;
    }

    private SecureSAXTransformerFactory( String factoryClassName, ClassLoader classloader )
    {
        this.factory = newSecureSAXTransformerFactory(factoryClassName, classloader);
    }

    /**
     * Create a new instance of {@linkplain SecureSAXTransformerFactory}
     * 
     * @see SAXTransformerFactory#newInstance()
     * @return a new instance of {@linkplain SecureSAXTransformerFactory}
     */
    public static SecureSAXTransformerFactory newInstance()
    {
        return new SecureSAXTransformerFactory( null, null );
    }

    /**
     * Create a new instance of {@linkplain SecureSAXTransformerFactory}
     * 
     * @see SAXTransformerFactory#newInstance(String, ClassLoader)
     * @param factoryClassName fully qualified factory class name that provides
     *            implementation of
     *            {@linkplain javax.xml.transform.TransformerFactory}.
     * @param classloader {@linkplain ClassLoader} used to load the factory
     *            class. If <code>null</code> current {@linkplain Thread}'s
     *            context classLoader is used to load the factory class.
     * @return a new instance of {@linkplain SecureSAXTransformerFactory}
     */
    public static SecureSAXTransformerFactory newInstance( String factoryClassName, ClassLoader classloader )
    {
        return new SecureSAXTransformerFactory( factoryClassName, classloader );
    }

    @Override
    public TransformerHandler newTransformerHandler( Source src )
        throws TransformerConfigurationException
    {
        return this.factory.newTransformerHandler( src );
    }

    @Override
    public TransformerHandler newTransformerHandler( Templates templates )
        throws TransformerConfigurationException
    {
        return this.factory.newTransformerHandler( templates );
    }

    @Override
    public TransformerHandler newTransformerHandler()
        throws TransformerConfigurationException
    {
        return this.factory.newTransformerHandler();
    }

    @Override
    public TemplatesHandler newTemplatesHandler()
        throws TransformerConfigurationException
    {
        return this.factory.newTemplatesHandler();
    }

    @Override
    public XMLFilter newXMLFilter( Source src )
        throws TransformerConfigurationException
    {
        return this.factory.newXMLFilter( src );
    }

    @Override
    public XMLFilter newXMLFilter( Templates templates )
        throws TransformerConfigurationException
    {
        return this.factory.newXMLFilter( templates );
    }

    @Override
    public Transformer newTransformer( Source source )
        throws TransformerConfigurationException
    {
        return this.factory.newTransformer( source );
    }

    @Override
    public Transformer newTransformer()
        throws TransformerConfigurationException
    {
        return this.factory.newTransformer();
    }

    @Override
    public Templates newTemplates( Source source )
        throws TransformerConfigurationException
    {
        return this.factory.newTemplates( source );
    }

    @Override
    public Source getAssociatedStylesheet( Source source, String media, String title, String charset )
        throws TransformerConfigurationException
    {
        return this.factory.getAssociatedStylesheet( source, media, title, charset );
    }

    @Override
    public void setURIResolver( URIResolver resolver )
    {
        this.factory.setURIResolver( resolver );
    }

    @Override
    public URIResolver getURIResolver()
    {
        return this.factory.getURIResolver();
    }

    @Override
    public void setFeature( String name, boolean value )
        throws TransformerConfigurationException
    {
        this.factory.setFeature( name, value );
    }

    @Override
    public boolean getFeature( String name )
    {
        return this.factory.getFeature( name );
    }

    /**
     * Set attribute for factory object. All attributes defined on
     * ATTRIBUTE_MAP cannot have the default value changed. In the case a
     * default value defined by ATTRIBUTE_MAP is attempted to be changed,
     * an exception is raised.
     * @param name The attribute name, which is a fully-qualified URI.
     * @param value The requested value for the property.
     * @exception IllegalArgumentException If the feature value
     *            differs from what defined in ATTRIBUTE_MAP.
     */
    @Override
    public void setAttribute( String name, Object value )
        throws IllegalArgumentException
    {
        // don't penalize someone for doing the right thing
        if ( ATTRIBUTE_MAP.containsKey( name ) && !ATTRIBUTE_MAP.get( name ).equals( value ) )
        {
            throw new IllegalArgumentException( "Cannot overwrite security feature: " + name );
        }
        this.factory.setAttribute( name, value );
    }

    @Override
    public Object getAttribute( String name )
    {
        return this.factory.getAttribute( name );
    }

    @Override
    public void setErrorListener( ErrorListener listener )
    {
        this.factory.setErrorListener( listener );
    }

    @Override
    public ErrorListener getErrorListener()
    {
        return this.factory.getErrorListener();
    }
}
