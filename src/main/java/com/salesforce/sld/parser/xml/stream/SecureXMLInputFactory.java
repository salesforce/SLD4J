/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.parser.xml.stream;

import javax.xml.stream.XMLInputFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Secured XMLInputFactory Decorator/Wrapper. It implements a set of properties
 * on the wrapped factory that enforce best security practices. This wrapper
 * disables the ability to use DTDs. This property cannot be overwritten
 * <p>
 * During parsing, the created XMLStreamReader will report an XMLStreamException
 * if a file attempts an illegal operation
 * 
 * @author csmith
 */
public class SecureXMLInputFactory
    extends AbstractXMLInputFactoryWrapper
{

    private static Map<String, Boolean> FEATURE_MAP = setupFeatureMap();

    private SecureXMLInputFactory( String factoryId, ClassLoader classLoader )
    {
        super( factoryId, classLoader );
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

        // This disables DTDs entirely for the factory
        features.put(XMLInputFactory.SUPPORT_DTD, false);

        // disable external entities
        features.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

        return Collections.unmodifiableMap( features );
    }

    /**
     * Set property for XMLInputFactory object. All properties defined
     * on FEATURE_MAP cannot have the default value changed. In the case a
     * default value defined by FEATURE_MAP is attempted to be changed,
     * an exception is raised.
     * @param reader {@linkplain XMLInputFactory} object.
     * @param name The feature name, which is a fully-qualified URI.
     * @param value The value of the property.
     * @exception java.lang.IllegalArgumentException If the feature value
     *            differs from what defined in FEATURE_MAP.
     */
    private void setProperty( XMLInputFactory reader, String name, Object value )
            throws IllegalArgumentException
    {
        // don't penalize someone for doing the right thing
        if ( FEATURE_MAP.containsKey( name ) && !FEATURE_MAP.get( name ).equals( value ) )
        {
            throw new IllegalArgumentException( "Cannot overwrite security feature: " + name );
        }

        reader.setProperty( name, value );

    }

    /**
     * Create a new instance of {@linkplain SecureXMLInputFactory}
     * 
     * @see XMLInputFactory#newFactory()
     * @return a new instance of {@linkplain SecureXMLInputFactory}
     */
    public static SecureXMLInputFactory newInstance()
    {
        return new SecureXMLInputFactory( null, null );
    }

    /**
     * Create a new instance of {@linkplain SecureXMLInputFactory} using the
     * supplied factory ID. If the factoryID is null, the default fallback
     * factory will be created
     * 
     * @see XMLInputFactory#newFactory(String, ClassLoader)
     * @param factoryId Name of the factory to find, same as a property name
     * @param classLoader classLoader to use
     * @return a new instance of {@linkplain SecureXMLInputFactory}
     */
    public static SecureXMLInputFactory newInstance( String factoryId, ClassLoader classLoader )
    {
        return new SecureXMLInputFactory( factoryId, classLoader );
    }

    /**
     * Create a new instance of {@linkplain SecureXMLInputFactory}
     * 
     * @see XMLInputFactory#newFactory()
     * @return a new instance of {@linkplain SecureXMLInputFactory}
     */
    public static SecureXMLInputFactory newFactory()
    {
        return new SecureXMLInputFactory( null, null );
    }

    /**
     * Create a new instance of {@linkplain SecureXMLInputFactory} using the
     * supplied factory ID. If the factoryID is null, the default fallback
     * factory will be created
     * 
     * @see XMLInputFactory#newFactory(String, ClassLoader)
     * @param factoryId Name of the factory to find, same as a property name
     * @param classLoader classLoader to use
     * @return a new instance of {@linkplain SecureXMLInputFactory}
     */
    public static SecureXMLInputFactory newFactory( String factoryId, ClassLoader classLoader )
    {
        return new SecureXMLInputFactory( factoryId, classLoader );
    }

    /**
     * Set all secure properties defined on FEATURE_MAP for XMLInputFactory .
     * @param factory {@linkplain XMLInputFactory} object.
     * @exception java.lang.IllegalArgumentException If the feature value
     *            differs from what defined in FEATURE_MAP.
     */
    @Override
    protected void setupWrapper( XMLInputFactory factory )
    {
        for ( Map.Entry<String, Boolean> entry : FEATURE_MAP.entrySet() )
        {
            try
            {
                factory.setProperty( entry.getKey(), entry.getValue() );
            }
            catch ( IllegalArgumentException e )
            {
                // Allow these exceptions as it means the security features
                // can't be mis-set
            }
        }
    }

    @Override
    public void setProperty( String name, Object value )
        throws IllegalArgumentException
    {
        setProperty(this.factory, name, value);
    }

    /**
     * Query the set of properties that this factory supports. All
     * properties defined on FEATURE_MAP are marked as not supported.
     *
     * @param name The name of the property (may not be null)
     * @return true if the property is supported and false otherwise
     */
    @Override
    public boolean isPropertySupported( String name )
    {
        if ( FEATURE_MAP.containsKey( name ) )
        {
            return false;
        }
        return this.factory.isPropertySupported( name );
    }

}
