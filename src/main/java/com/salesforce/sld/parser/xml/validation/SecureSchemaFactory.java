/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.parser.xml.validation;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.SchemaFactoryConfigurationError;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Secured SchemaFactory wrapper. It implements a set of properties on the
 * wrapped factory that enforce best security practices. This wrapper disables
 * the ability to access external DTDs and schemas. These property cannot be
 * overwritten
 * <p>
 * During parsing, the created Schema will report a SAXParseException if a
 * schema attempts an illegal operation
 * 
 * @author csmith
 */
public class SecureSchemaFactory
    extends SchemaFactory
{
    private static Map<String, String> ATTRIBUTE_MAP = setupAttributeMap();

    private final SchemaFactory factory;

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
        attributes.put(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "all");

        return Collections.unmodifiableMap( attributes );
    }

    /**
     * <p>Obtain a new instance of a <code>SchemaFactory</code>.
     * This method creates a new factory instance.</p>
     * @param schemaLanguage fully qualified factory class name that provides implementation of SAXTransformerFactory.
     *
     * @param factoryClassName fully qualified factory class name that provides
     *            implementation of
     *            {@linkplain javax.xml.transform.TransformerFactory}.
     *
     * @param classLoader <code>ClassLoader</code> used to load the factory class. If <code>null</code>
     *                     current <code>Thread</code>'s context classLoader is used to load the factory class.
     * @return new SchemaFactory instance.
     *
     */
    private SchemaFactory newSecureSchemaFactory( String schemaLanguage, String factoryClassName,
                                                 ClassLoader classLoader )
            throws IllegalArgumentException, NullPointerException,
            SchemaFactoryConfigurationError
    {
        SchemaFactory sFactory;

        if ( factoryClassName != null )
        {
            sFactory = SchemaFactory.newInstance( schemaLanguage, factoryClassName, classLoader );
        }
        else
        {
            sFactory = SchemaFactory.newInstance( schemaLanguage );
        }

        for ( Map.Entry<String, String> entry : ATTRIBUTE_MAP.entrySet() )
        {
            try
            {
                sFactory.setProperty( entry.getKey(), entry.getValue() );
            }
            catch ( SAXException e )
            {
                // Allow these exceptions as it means the security features
                // can't be mis-set
            }
        }

        return sFactory;
    }

    private SecureSchemaFactory( String schemaLanguage, String factoryClassName, ClassLoader classLoader )
    {
        this.factory = newSecureSchemaFactory(schemaLanguage, factoryClassName, classLoader);
    }

    /**
     * Produces a new {@linkplain SecureSchemaFactory} using the default
     * {@link XMLConstants#W3C_XML_SCHEMA_NS_URI} schema
     * 
     * @see SchemaFactory#newInstance(String)
     * @return New instance of a {@linkplain SecureSchemaFactory}
     * @throws IllegalArgumentException If no implementation of the schema
     *             language is available.
     * @throws NullPointerException If the <code>schemaLanguage</code> parameter
     *             is null.
     * @throws SchemaFactoryConfigurationError If a configuration error is
     *             encountered.
     */
    public static SecureSchemaFactory newInstance()
    {
        return new SecureSchemaFactory( XMLConstants.W3C_XML_SCHEMA_NS_URI, null, null );
    }

    /**
     * @see SchemaFactory#newInstance(String)
     * @param schemaLanguage Specifies the schema language which the returned
     *            SchemaFactory will understand. See
     *            <a href="#schemaLanguage">the list of available schema
     *            languages</a> for the possible values.
     * @return New instance of a {@linkplain SecureSchemaFactory}
     * @throws IllegalArgumentException If no implementation of the schema
     *             language is available.
     * @throws NullPointerException If the <code>schemaLanguage</code> parameter
     *             is null.
     * @throws SchemaFactoryConfigurationError If a configuration error is
     *             encountered.
     */
    public static SecureSchemaFactory newInstance( String schemaLanguage )
    {
        return new SecureSchemaFactory( schemaLanguage, null, null );
    }

    /**
     * @see SchemaFactory#newInstance(String, String, ClassLoader)
     * @param schemaLanguage Specifies the schema language which the returned
     *            {@linkplain SchemaFactory} will understand. See
     *            <a href="#schemaLanguage">the list of available schema
     *            languages</a> for the possible values.
     * @param factoryClassName fully qualified factory class name that provides
     *            implementation of
     *            {@linkplain javax.xml.validation.SchemaFactory}.
     * @param classLoader {@linkplain ClassLoader} used to load the factory
     *            class. If <code>null</code> current {@linkplain Thread}'s
     *            context classLoader is used to load the factory class.
     * @return New instance of a {@linkplain SecureSchemaFactory}
     * @throws IllegalArgumentException if <code>factoryClassName</code> is
     *             <code>null</code>, or the factory class cannot be loaded,
     *             instantiated or doesn't support the schema language specified
     *             in <code>schemLanguage</code> parameter.
     * @throws NullPointerException If the <code>schemaLanguage</code> parameter
     *             is null.
     */
    public static SecureSchemaFactory newInstance( String schemaLanguage, String factoryClassName,
                                                   ClassLoader classLoader )
    {
        return new SecureSchemaFactory( schemaLanguage, factoryClassName, classLoader );
    }

    /**
     * Set property for factory object. All properties defined on
     * ATTRIBUTE_MAP cannot have the default value changed. In the case a
     * default value defined by ATTRIBUTE_MAP is attempted to be changed,
     * an exception is raised.
     * @param name The attribute name, which is a fully-qualified URI.
     * @param object The value of the property
     * @exception java.lang.IllegalArgumentException If the attribute value
     *            differs from what defined in ATTRIBUTE_MAP.
     */
    @Override
    public void setProperty( String name, Object object )
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        // don't penalize someone for doing the right thing
        if ( ATTRIBUTE_MAP.containsKey( name ) && !ATTRIBUTE_MAP.get( name ).equals( object ) )
        {
            throw new SAXNotSupportedException( "Cannot overwrite security feature: " + name );
        }
        this.factory.setProperty( name, object );
    }

    @Override
    public Object getProperty( String name )
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        return this.factory.getProperty( name );
    }

    @Override
    public void setFeature( String name, boolean value )
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        this.factory.setFeature( name, value );
    }

    @Override
    public boolean getFeature( String name )
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        return this.factory.getFeature( name );
    }

    @Override
    public boolean isSchemaLanguageSupported( String schemaLanguage )
    {
        return this.factory.isSchemaLanguageSupported( schemaLanguage );
    }

    @Override
    public void setErrorHandler( ErrorHandler errorHandler )
    {
        this.factory.setErrorHandler( errorHandler );
    }

    @Override
    public ErrorHandler getErrorHandler()
    {
        return this.factory.getErrorHandler();
    }

    @Override
    public void setResourceResolver( LSResourceResolver resourceResolver )
    {
        this.factory.setResourceResolver( resourceResolver );
    }

    @Override
    public LSResourceResolver getResourceResolver()
    {
        return this.factory.getResourceResolver();
    }

    @Override
    public Schema newSchema( Source[] source )
        throws SAXException
    {
        return this.factory.newSchema( source );
    }

    @Override
    public Schema newSchema()
        throws SAXException
    {
        return this.factory.newSchema();
    }
}
