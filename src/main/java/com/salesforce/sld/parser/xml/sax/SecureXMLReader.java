/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.parser.xml.sax;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Secured XMLReader wrapper. It implements a set of features on the wrapped
 * reader that enforce best security practices. This wrapper disables the
 * ability to resolve external dtds and entities. These features cannot be
 * overwritten
 * <p>
 * During parsing, the created XMLReader will report nothing if a file attempts
 * an illegal operation, as the parser ignores all illegal data in the document
 * 
 * @author csmith
 */
public class SecureXMLReader
    implements XMLReader
{
    private static Map<String, Boolean> FEATURE_MAP = setupFeatureMap();

    private final XMLReader xmlReader;

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
     * Set all secure properties defined on FEATURE_MAP for XMLReader .
     * @param reader {@linkplain XMLReader} object.
     */
    private void setSecurityFeatures(XMLReader reader)
    {
        for ( Map.Entry<String, Boolean> entry : FEATURE_MAP.entrySet() )
        {
            try
            {
                reader.setFeature( entry.getKey(), entry.getValue() );
            }
            catch ( SAXException e )
            {
                // Allow these exceptions as it means the security features
                // can't be mis-set
            }
        }
    }

    /**
     * Constructs a new {@linkplain SecureXMLReader}
     * 
     * @see XMLReaderFactory#createXMLReader()
     * @throws SAXException If no default XMLReader class can be identified and
     *             instantiated.
     */
    public SecureXMLReader()
        throws SAXException
    {
        this.xmlReader = newSecureXMLReader( null );
        setSecurityFeatures(this.xmlReader);
    }

    /**
     * Constructs a new {@linkplain SecureXMLReader} using a supplied XMLReader.
     * Overwrites Features if not securely configured
     * 
     * @param readerInstance an {@linkplain XMLReader} to use as the wrapped
     *            object
     * @throws SAXException if
     */
    public SecureXMLReader( XMLReader readerInstance )
        throws SAXException
    {
        this.xmlReader = readerInstance;
        setSecurityFeatures(this.xmlReader);
    }

    /**
     * Constructs a new {@linkplain SecureXMLReader} using the supplied
     * classname
     * 
     * @see XMLReaderFactory#createXMLReader(String)
     * @param className the fully qualified class of an XMLReader
     * @throws SAXException If the class cannot be loaded, instantiated, and
     *             cast to XMLReader.
     */
    public SecureXMLReader( String className )
        throws SAXException
    {
        this.xmlReader = newSecureXMLReader( className );
        setSecurityFeatures(this.xmlReader);
    }

    private XMLReader newSecureXMLReader( String className )
        throws SAXException
    {
        XMLReader xmlReader;

        if ( className != null )
        {
            xmlReader = XMLReaderFactory.createXMLReader( className );
        }
        else
        {
            xmlReader = XMLReaderFactory.createXMLReader();
        }

        return xmlReader;
    }

    @Override
    public boolean getFeature( String name )
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        return this.xmlReader.getFeature( name );
    }

    /**
     * Set feature for XMLReader object. All features defined on FEATURE_MAP
     * cannot have the default value changed. In the case a default value
     * defined by FEATURE_MAP is attempted to be changed, an exception is
     * raised.
     * @param name The feature name, which is a fully-qualified URI.
     * @param value The requested value of the feature (true or false).
     * @exception SAXNotRecognizedException If the feature
     *            is not found in FEATURE_MAP.
     * @exception SAXNotSupportedException If the feature
     *            value differs from what defined in FEATURE_MAP.
     */
    @Override
    public void setFeature( String name, boolean value )
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        // don't penalize someone for doing the right thing
        if ( FEATURE_MAP.containsKey( name ) && FEATURE_MAP.get( name ) != value )
        {
            throw new SAXNotSupportedException( "Cannot overwrite security feature: " + name );
        }
        this.xmlReader.setFeature( name, value );
    }

    @Override
    public Object getProperty( String name )
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        return this.xmlReader.getProperty( name );
    }

    @Override
    public void setProperty( String name, Object value )
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        this.xmlReader.setProperty( name, value );
    }

    @Override
    public void setEntityResolver( EntityResolver resolver )
    {
        this.xmlReader.setEntityResolver( resolver );
    }

    @Override
    public EntityResolver getEntityResolver()
    {
        return this.xmlReader.getEntityResolver();
    }

    @Override
    public void setDTDHandler( DTDHandler handler )
    {
        this.xmlReader.setDTDHandler( handler );
    }

    @Override
    public DTDHandler getDTDHandler()
    {
        return this.xmlReader.getDTDHandler();
    }

    @Override
    public void setContentHandler( ContentHandler handler )
    {
        this.xmlReader.setContentHandler( handler );
    }

    @Override
    public ContentHandler getContentHandler()
    {
        return this.xmlReader.getContentHandler();
    }

    @Override
    public void setErrorHandler( ErrorHandler handler )
    {
        this.xmlReader.setErrorHandler( handler );
    }

    @Override
    public ErrorHandler getErrorHandler()
    {
        return this.xmlReader.getErrorHandler();
    }

    @Override
    public void parse( InputSource input )
        throws IOException, SAXException
    {
        this.xmlReader.parse( input );
    }

    @Override
    public void parse( String systemId )
        throws IOException, SAXException
    {
        this.xmlReader.parse( systemId );
    }
}
