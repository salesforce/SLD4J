/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.parser.xml.parsers;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A Secured DBF wrapper. It implements a set of features on the wrapped factory
 * that enforce best security practices. This wrapper disables the ability to
 * declare DOCTYPEs, remove ENTITY declarations inline, and load DTDs
 * externally. These features cannot be overwritten.
 * <p>
 * During parsing, the created DocumentBuilder will report a SAXException if an
 * xml doc contains an illegal declaration.
 * 
 * @author csmith
 */
public class SecureDocumentBuilderFactory
    extends DocumentBuilderFactory
{
    private static Map<String, Boolean> FEATURE_MAP = setupFeatureMap();

    protected final DocumentBuilderFactory factory;

    protected SecureDocumentBuilderFactory( String factoryClassName, ClassLoader classLoader )
    {
        this.factory = newDBFWrapperInstance(factoryClassName, classLoader);
    }

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

        // This is the PRIMARY defense. If DTDs (doctypes) are disallowed,
        // almost all XML entity attacks are prevented
        // Xerces 2 only -
        // http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
        features.put("http://apache.org/xml/features/disallow-doctype-decl", true);

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
     * Set feature for DocumentBuilderFactory object. All features defined
     * on FEATURE_MAP cannot have the default value changed. In the case a
     * default value defined by FEATURE_MAP is attempted to be changed,
     * an exception is raised.
     * @param reader {@linkplain DocumentBuilderFactory} object.
     * @param name The feature name, which is a fully-qualified URI.
     * @param value The requested value of the feature (true or false).
     * @exception javax.xml.parsers.ParserConfigurationException If the
     *            feature value differs from what defined in FEATURE_MAP.
     */
    private void setFeature(DocumentBuilderFactory reader, String name, boolean value )
            throws ParserConfigurationException
    {
        // don't penalize someone for doing the right thing
        if ( FEATURE_MAP.containsKey( name ) && FEATURE_MAP.get( name ) != value )
        {
            throw new ParserConfigurationException( "Cannot overwrite security feature: " + name );
        }

        reader.setFeature( name, value );
    }

    /**
     * <p>Obtain a new instance of a <code>DocumentBuilderFactory</code>.
     * This method creates a new factory instance.</p>
     * @param factoryClassName fully qualified factory class name that provides implementation of DocumentBuilderFactory.
     *
     * @param classLoader <code>ClassLoader</code> used to load the factory class. If <code>null</code>
     *                     current <code>Thread</code>'s context classLoader is used to load the factory class.
     * @return new SchemaFactory instance.
     *
     */
    private DocumentBuilderFactory newDBFWrapperInstance( String factoryClassName, ClassLoader classLoader )
    {

        DocumentBuilderFactory dbf;
        if ( factoryClassName != null )
        {
            dbf = DocumentBuilderFactory.newInstance( factoryClassName, classLoader );
        }
        else
        {
            dbf = DocumentBuilderFactory.newInstance();
        }

        for ( Map.Entry<String, Boolean> entry : FEATURE_MAP.entrySet() )
        {
            try
            {
                dbf.setFeature( entry.getKey(), entry.getValue() );
            }
            catch ( ParserConfigurationException e )
            {
                // Allow these exceptions as it means the security features
                // can't be mis-set
            }
        }

        // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD,
        // and Entity Attacks"
        try
        {
            dbf.setXIncludeAware( false );
        }
        catch ( UnsupportedOperationException e )
        {
            // and allow this one
        }

        // this doesn't throw an exception by default
        dbf.setExpandEntityReferences( false );

        return dbf;
    }

    /**
     * Create a new instance of the {@linkplain SecureDocumentBuilderFactory}
     * 
     * @see DocumentBuilderFactory#newInstance()
     * @return New instance of a {@linkplain SecureDocumentBuilderFactory}
     */
    public static SecureDocumentBuilderFactory newInstance()
    {
        return new SecureDocumentBuilderFactory( null, null );
    }

    /**
     * Create a new instance of the {@linkplain SecureDocumentBuilderFactory}
     * using the specified classname and classloader
     * <p>
     * if the classname is null, the default fallback factory is loaded instead
     * 
     * @see DocumentBuilderFactory#newInstance(String, ClassLoader)
     * @param factoryClassName fully qualified factory class name that provides
     *            implementation of
     *            {@linkplain javax.xml.parsers.DocumentBuilderFactory}.
     * @param classLoader {@linkplain ClassLoader} used to load the factory
     *            class. If <code>null</code> current {@linkplain Thread}'s
     *            context classLoader is used to load the factory class.
     * @return New instance of a {@linkplain SecureDocumentBuilderFactory}
     */
    public static SecureDocumentBuilderFactory newInstance( String factoryClassName, ClassLoader classLoader )
    {
        return new SecureDocumentBuilderFactory( factoryClassName, classLoader );
    }

    /**
     * Create a new instance of the {@linkplain DocumentBuilder}
     * setting secure features to prevent XXE issues.
     * <p>
     * The secure features are defined at the private method
     * SecureDocumentBuilderFactory#setupFeatureMap(). The secure
     * features basically disable DTDs (doctypes) and external
     * DTDs by using the following settings:
     *
     * Feature enabled:
     * http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
     *
     * Feature disabled:
     * http://xml.org/sax/features/external-general-entities
     *
     * Feature disabled:
     * http://xml.org/sax/features/external-parameter-entities
     *
     * Feature disabled:
     * http://apache.org/xml/features/nonvalidating/load-external-dtd
     *
     * @return New instance of a {@linkplain DocumentBuilder}
     */
    @Override
    public DocumentBuilder newDocumentBuilder()
        throws ParserConfigurationException
    {
        DocumentBuilder safeDocBuilder = this.factory.newDocumentBuilder();
        return safeDocBuilder;
    }

    /**
     * Set state of XInclude processing.
     *
     * XInclude processing defaults to <code>false</code>.
     *
     * @param value Set XInclude processing to <code>true</code> or
     *   <code>false</code>
     *
     * @throws IllegalArgumentException When setting value include aware to
     * any value other than default false.
     */
    @Override
    public void setXIncludeAware( boolean value )
        throws IllegalArgumentException
    {
        if ( value )
        {
            throw new IllegalArgumentException( "Cannot set X include aware to any value other than default false" );
        }
    }

    /**
     * Specifies that the parser produced by this code will
     * expand entity reference nodes. By default the value of this is set to
     * <code>true</code>
     *
     * @param value true if the parser produced will expand entity
     *                        reference nodes; false otherwise.
     *
     * @throws IllegalArgumentException When setting value to any value other than
     * default false.
     */
    @Override
    public void setExpandEntityReferences( boolean value )
        throws IllegalArgumentException
    {
        if ( value )
        {
            throw new IllegalArgumentException( "Cannot set entity reference to any value other than default false" );
        }
    }

    /**
     * Enable/disable a feature for the
     * {@linkplain DocumentBuilder} object. This function
     * ensures that none of the secure features used to
     * prevent XXE can be modified.
     * <p>
     * The XXE secure features are set by the constructor when
     * the object is instantiated.
     * @see DocumentBuilderFactory#newDocumentBuilder() for the
     * list of secure features set by default.
     * <p>
     * In an attempt to change the default value of a secure
     * feasture, a ParserConfigurationException exception will
     * be thrown.
     *
     * @param name {@linkplain DocumentBuilder} feature to be
     *            changed.
     * @param value new value to enable/disable the feature.
     */
    @Override
    public void setFeature( String name, boolean value )
        throws ParserConfigurationException
    {
        setFeature(this.factory, name, value);
    }

    @Override
    public void setAttribute( String name, Object value )
        throws IllegalArgumentException
    {
        this.factory.setAttribute( name, value );
    }

    @Override
    public Object getAttribute( String name )
        throws IllegalArgumentException
    {
        return this.factory.getAttribute( name );
    }

    @Override
    public boolean getFeature( String name )
        throws ParserConfigurationException
    {
        return this.factory.getFeature( name );
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
    public void setCoalescing( boolean coalescing )
    {
        this.factory.setCoalescing( coalescing );
    }

    @Override
    public boolean isCoalescing()
    {
        return this.factory.isCoalescing();
    }

    @Override
    public void setIgnoringComments( boolean ignoreComments )
    {
        this.factory.setIgnoringComments( ignoreComments );
    }

    @Override
    public boolean isIgnoringComments()
    {
        return this.factory.isIgnoringComments();
    }

    @Override
    public void setIgnoringElementContentWhitespace( boolean whitespace )
    {
        this.factory.setIgnoringElementContentWhitespace( whitespace );
    }

    @Override
    public boolean isIgnoringElementContentWhitespace()
    {
        return this.factory.isIgnoringComments();
    }

    @Override
    public void setValidating( boolean validating )
    {
        this.factory.setValidating( validating );
    }

    @Override
    public boolean isExpandEntityReferences()
    {
        return this.factory.isExpandEntityReferences();
    }

    @Override
    public boolean isXIncludeAware()
    {
        return this.factory.isXIncludeAware();
    }

    @Override
    public void setNamespaceAware( boolean awareness )
    {
        this.factory.setNamespaceAware( awareness );
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
