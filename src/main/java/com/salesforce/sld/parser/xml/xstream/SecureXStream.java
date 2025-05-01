/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.parser.xml.xstream;

import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.salesforce.sld.foundation.exception.SecurityControlRuntimeException;
import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.security.ArrayTypePermission;
import com.thoughtworks.xstream.security.ExplicitTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import com.thoughtworks.xstream.security.TypeHierarchyPermission;
import com.thoughtworks.xstream.security.TypePermission;

/**
 * SecureXStream is an XStream decorator that provides a secure implementation for unmarshalling XML.
 * <h2>SecureXStream as standard</h2>
 *
 * Unlike XStream, SecureXStream automatically assigns the NoTypePermission and sets up permissions for safe and often
 * used classes. This includes primitives, Arrays (where class's isArray() returns true), null objects, the
 * {@linkplain Collection} hierarchy, the {@linkplain Map} hierarchy, and the {@linkplain String} class.<br>
 *
 * Types can be allowed by using the allow methods that SecureXStream provides:
 * {@linkplain SecureXStream#allowTypes(String[])}, {@linkplain SecureXStream#allowTypes(Class[])},
 * {@linkplain SecureXStream#allowTypeHierarchy(Class)}. These allow methods were adapted with security in mind and do
 * not allow class types that pose a security risk.
 *
 *
 * <b>Usage example:</b>
 * 
 * <pre>
 * public unmarshall( String xml )
 * {
 *     SecureXStream xstream = new SecureXStream();
 *     xstream.allowTypes( new Class[] { Square.class, Dimensions.class } );
 *     Square square = (Square) xstream.fromXML( xml );
 *     System.out.println( "Width: " + square.getDimensions().getWidth() );
 * }
 * </pre>
 *
 *
 * 
 * @author ddwyer
 */
public class SecureXStream
    extends XStream
{
    private static final String XSTREAMPACKAGE = "com.thoughtworks.xstream";

    private static List<Class<? extends TypePermission>> allowedPermissions =
        new ArrayList<Class<? extends TypePermission>>( Arrays.asList( NoTypePermission.class,
            PrimitiveTypePermission.class, NullPermission.class, ArrayTypePermission.class ) );

    private static List<Class<?>> prohibitedClasses =
        new ArrayList<Class<?>>( Arrays.asList( InvocationHandler.class ) );

    /**
     * Applies our basic security settings to the XStream object.
     *
     * This method adds permissions and set allowed safe classes to the outside initialised {@link XStream} instances.
     * This is a convenience method to apply basic security.<br>
     * The usage of this method is an exception to the standard!
     *
     *
     * <b>Prefer to use the {@link SecureXStream} constructor and the created object instead.</b> But if you need to add
     * custom {@link Converter}, use this method to add basic security.
     *
     * <i>Usage example:</i>
     *
     * <pre>
     * public MyXmlObject initialiseXStreamSecurity()
     * {
     *     XStream xstream = SecureXStream.applySecuritySettings( new XStream() );
     *     xstream.registerConverter( new MyXmlObjectConverter() );
     *
     *     MyXmlObject result = new MyXmlObject();
     *
     *     try (InputStream input = new BufferedReader( new InputStreamReader( System.in ) ))
     *     {
     *         xstream.fromXML( input, result );
     *     }
     *
     *     return result;
     * }
     * </pre>
     *
     * @param xstream The {@link XStream} object. This cannot be <code>null</code>!
     * @return The security applied {@link XStream} object.
     */
    public static XStream applySecuritySettings( XStream xstream )
    {
        Preconditions.checkNotNull( xstream, "XStream may not be NULL!" );

        xstream.addPermission( NoTypePermission.NONE );
        xstream.addPermission( PrimitiveTypePermission.PRIMITIVES );
        xstream.addPermission( ArrayTypePermission.ARRAYS );
        xstream.addPermission( NullPermission.NULL );

        // Set up default safe classes
        xstream.allowTypeHierarchy( Collection.class );
        xstream.allowTypeHierarchy( Map.class );
        xstream.allowTypes( new Class[] { String.class } );

        return xstream;
    }

    /**
     * Constructs a default SecureXStream.
     *
     * The instance will use the {@link XppDriver} as default and tries to determine the best match for the
     * {@link ReflectionProvider} on its own. SecureXStream overrides setupSecurity to only allow primitives, Arrays
     * (class type's isArray() returns true), null objects, the {@linkplain Collection} hierarchy, the {@linkplain Map}
     * hierarchy, and the {@linkplain String} class by default. All other classes must be allowed using
     * {@linkplain SecureXStream#allowTypes(String[])}, {@linkplain SecureXStream#allowTypes(Class[])}, or
     * {@linkplain SecureXStream#allowTypeHierarchy(Class)}.
     *
     *
     * @throws InitializationException in case of an initialization problem
     */
    public SecureXStream()
    {
        super();
    }

    /**
     * Constructs an SecureXStream with a special {@link ReflectionProvider}.
     *
     * The instance will use the {@link XppDriver} as default. SecureXStream overrides setupSecurity to only allow
     * primitives, Arrays (class type's isArray() returns true), null objects, the {@linkplain Collection} hierarchy,
     * the {@linkplain Map} hierarchy, and the {@linkplain String} class by default. All other classes must be allowed
     * using {@linkplain SecureXStream#allowTypes(String[])}, {@linkplain SecureXStream#allowTypes(Class[])}, or
     * {@linkplain SecureXStream#allowTypeHierarchy(Class)}.
     *
     * 
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching reflection provider
     * @throws InitializationException in case of an initialization problem
     */
    public SecureXStream( ReflectionProvider reflectionProvider )
    {
        super( reflectionProvider );
    }

    /**
     * Constructs an SecureXStream with a special {@link HierarchicalStreamDriver}.
     *
     * The instance will tries to determine the best match for the {@link ReflectionProvider} on its own. SecureXStream
     * overrides setupSecurity to only allow primitives, Arrays (class type's isArray() returns true), null objects, the
     * {@linkplain Collection} hierarchy, the {@linkplain Map} hierarchy, and the {@linkplain String} class by default.
     * All other classes must be allowed using {@linkplain SecureXStream#allowTypes(String[])},
     * {@linkplain SecureXStream#allowTypes(Class[])}, or {@linkplain SecureXStream#allowTypeHierarchy(Class)}.
     *
     *
     * @param hierarchicalStreamDriver the driver instance
     * @throws InitializationException in case of an initialization problem
     */
    public SecureXStream( HierarchicalStreamDriver hierarchicalStreamDriver )
    {
        super( hierarchicalStreamDriver );
    }

    /**
     * Constructs an SecureXStream with a special {@link HierarchicalStreamDriver} and {@link ReflectionProvider}.
     * SecureXStream overrides setupSecurity to only allow primitives, Arrays (class type's isArray() returns true),
     * null objects, the {@linkplain Collection} hierarchy, the {@linkplain Map} hierarchy, and the {@linkplain String}
     * class by default. All other classes must be allowed using {@linkplain SecureXStream#allowTypes(String[])},
     * {@linkplain SecureXStream#allowTypes(Class[])}, or {@linkplain SecureXStream#allowTypeHierarchy(Class)}.
     * 
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching Provider
     * @param hierarchicalStreamDriver the driver instance
     * @throws InitializationException in case of an initialization problem
     */
    public SecureXStream( ReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver )
    {
        super( reflectionProvider, hierarchicalStreamDriver );
    }

    /**
     * Constructs an SecureXStream with a special {@link HierarchicalStreamDriver}, {@link ReflectionProvider} and a
     * {@link ClassLoaderReference}. SecureXStream overrides setupSecurity to only allow primitives, Arrays (class
     * type's isArray() returns true), null objects, the {@linkplain Collection} hierarchy, the {@linkplain Map}
     * hierarchy, and the {@linkplain String} class by default. All other classes must be allowed using
     * {@linkplain SecureXStream#allowTypes(String[])}, {@linkplain SecureXStream#allowTypes(Class[])}, or
     * {@linkplain SecureXStream#allowTypeHierarchy(Class)}.
     * 
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching Provider
     * @param driver the driver instance
     * @param classLoaderReference the reference to the {@link ClassLoader} to use
     * @throws InitializationException in case of an initialization problem
     * @since 1.4.5
     */
    public SecureXStream( ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                          ClassLoaderReference classLoaderReference )
    {
        super( reflectionProvider, driver, classLoaderReference );
    }

    /**
     * Constructs an SecureXStream with a special {@link HierarchicalStreamDriver}, {@link ReflectionProvider} and the
     * {@link ClassLoader} to use. SecureXStream overrides setupSecurity to only allow primitives, Arrays (class type's
     * isArray() returns true), null objects, the {@linkplain Collection} hierarchy, the {@linkplain Map} hierarchy, and
     * the {@linkplain String} class by default. All other classes must be allowed using
     * {@linkplain SecureXStream#allowTypes(String[])}, {@linkplain SecureXStream#allowTypes(Class[])}, or
     * {@linkplain SecureXStream#allowTypeHierarchy(Class)}.
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching Provider
     * @param driver the driver instance
     * @param classLoader object {@link ClassLoader} to use
     * @throws InitializationException in case of an initialization problem
     * @since 1.3
     * @deprecated As of 1.4.5 use
     *             {@link #SecureXStream(ReflectionProvider, HierarchicalStreamDriver, ClassLoaderReference)}
     */
    public SecureXStream( ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                          ClassLoader classLoader )
    {
        super( reflectionProvider, driver, classLoader );
    }

    @Override
    protected void setupSecurity()
    {
        applySecuritySettings( this );
    }

    /**
     * @throws SecurityControlRuntimeException if method is called with a custom Converter
     */
    @Override
    public void registerConverter( Converter converter, int priority )
        throws SecurityControlRuntimeException
    {
        if ( !converter.getClass().getName().startsWith( XSTREAMPACKAGE ) )
        {
            throw new SecurityControlRuntimeException( "Registering custom Converters is not permitted." );
        }
        super.registerConverter( converter, priority );
    }

    /**
     * @throws SecurityControlRuntimeException if method is called with a custom SingleValueConverter
     */
    @Override
    public void registerConverter( SingleValueConverter converter, int priority )
        throws SecurityControlRuntimeException
    {
        if ( !converter.getClass().getName().startsWith( XSTREAMPACKAGE ) )
        {
            throw new SecurityControlRuntimeException( "Registering custom SingleValueConverters is not permitted." );
        }
        super.registerConverter( converter, priority );
    }

    /**
     * @throws SecurityControlRuntimeException if method is called with a custom Converter
     */
    @Override
    public void registerLocalConverter( Class definedIn, String fieldName, Converter converter )
        throws SecurityControlRuntimeException
    {
        if ( !converter.getClass().getName().startsWith( XSTREAMPACKAGE ) )
        {
            throw new SecurityControlRuntimeException( "Registering custom Converters is not permitted." );
        }
        super.registerLocalConverter( definedIn, fieldName, converter );
    }

    /**
     * Check if the name of the class poses a security risk. If it does, throw a security runtime exception.
     * 
     * @param name - name of class to be checked
     * @throws SecurityControlRuntimeException if class name poses a security risk.
=     */
    private void validateName( String name )
        throws SecurityControlRuntimeException
    {
        Class<?> namedClass;
        try
        {
            namedClass = Class.forName( name );
        }
        catch ( ClassNotFoundException e )
        {
            throw new SecurityControlRuntimeException( "Class " + name + " cannot be found." );
        }
        for ( Class<?> clazz : prohibitedClasses )
        {
            if ( clazz.isAssignableFrom( namedClass ) )
            {
                throw new SecurityControlRuntimeException( "Class " + namedClass.getName() + " allows for the class " + clazz.getName()
                    + " to be unmarshalled and poses a security risk." );
            }
        }
    }

    /**
     * Check if the names of the classes pose a security risk. If it does, a security runtime exception will be thrown.
     * 
     * @param names - names of classes to be checked
     * @throws SecurityControlRuntimeException if class name poses a security risk.
     */
    private void validateNames( String... names )
        throws SecurityControlRuntimeException
    {
        if ( names != null )
        {
            for ( String name : names )
            {
                validateName( name );
            }
        }
    }

    /**
     * @throws SecurityControlRuntimeException if the names of the classes are not permitted
     */
    @Override
    public void allowTypes( String[] names )
        throws SecurityControlRuntimeException
    {
        validateNames( names );
        super.addPermission( new ExplicitTypePermission( names ) );
    }

    /**
     * @throws SecurityControlRuntimeException if the class types are not permitted
     */
    @Override
    public void allowTypes( Class[] types )
        throws SecurityControlRuntimeException
    {
        if ( types != null )
        {
            String[] names = new String[types.length];
            for ( int i = 0; i < types.length; ++i )
            {
                names[i] = types[i].getName();
            }
            allowTypes( names );
        }
    }

    /**
     * @throws SecurityControlRuntimeException if the type hierarchy of the class is not permitted
     */
    @Override
    public void allowTypeHierarchy( Class type )
        throws SecurityControlRuntimeException
    {
        if ( type.equals( Object.class ) )
        {
            throw new SecurityControlRuntimeException( "The Object class is too encompassing for a type hierarchy." );
        }
        validateNames( type.getName() );
        super.addPermission( new TypeHierarchyPermission( type ) );
    }

    /**
     * @throws SecurityControlRuntimeException if the method is called
     */
    @Override
    public void allowTypesByRegExp( String[] regexps )
        throws SecurityControlRuntimeException
    {
        throw new SecurityControlRuntimeException( "Allowing types through regular expressions is not permitted." );
    }

    /**
     * @throws SecurityControlRuntimeException if the method is called
     */
    @Override
    public void allowTypesByRegExp( Pattern[] regexps )
        throws SecurityControlRuntimeException
    {
        throw new SecurityControlRuntimeException( "Allowing types through regular expressions is not permitted." );
    }

    /**
     * @throws SecurityControlRuntimeException if the method is called
     */
    @Override
    public void allowTypesByWildcard( String[] patterns )
        throws SecurityControlRuntimeException
    {
        throw new SecurityControlRuntimeException( "Allowing types through wildcards is not permitted." );
    }

    /**
     * @throws SecurityControlRuntimeException if the permission provided is not permitted
     */
    @Override
    public void addPermission( TypePermission permission )
        throws SecurityControlRuntimeException
    {
        if ( allowedPermissions.contains( permission.getClass() ) )
        {
            super.addPermission( permission );
            return;
        }
        throw new SecurityControlRuntimeException(
            "The addPermission method is disabled for " + permission.getClass() + ". Please use the allow methods." );
    }
}