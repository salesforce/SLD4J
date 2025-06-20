package com.salesforce.sld.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.salesforce.sld.lang.deserialize.DeserializePermission;
import com.salesforce.sld.lang.deserialize.IDeserializePermission;

public class SecureObjectInputStream
    extends ObjectInputStream
    {
        /**
         * We supply the classloader of this class as a "catch-all" if the class
         * that is asked for to be deserialized uses a class that is only defined in
         * this context. This is an unexpected case.
         */
        private static final ClassLoader FALLBACK_CLASS_LOADER = SecureObjectInputStream.class.getClassLoader();

        /**
         * A Whitelist of allowed classes
         */
        private List<Class<?>> serializableClasses;

        /**
         * A list of permissions to check against a deserialization class
         */
        private List<IDeserializePermission> permissions;

        /**
         * The origination {@linkplain InputStream}'s classloader
         */
        private final ClassLoader inLoader;

        /**
         * Create a Secured {@linkplain ObjectInputStream} based on the supplied {@linkplain InputStream}. Also
         * configures the initial permissions which include the {@linkplain DeserializePermission#NONE} to clear the
         * permissions list, the {@linkplain DeserializePermission#ARRAY},
         * {@linkplain DeserializePermission#PRIMITIVES}, {@linkplain DeserializePermission#PROXY} to allow some basic
         * classtypes. The classes that are defined within the Proxy and Array must be manually allowed <br>
         * e.g. <code>String foo</code> is allowed as it is a "primitive", <code>String[] bar</code> is allowed as it is
         * a primitive in an Array, but <code>CustomObject[] baz</code> is not allowed until the CustomObject is added
         * to the allowed classes, but you do not have to explicitly permit the array version of this object.
         * 
         * @param in input stream to read from
         * @throws StreamCorruptedException if the stream header is incorrect
         * @throws IOException if an I/O error occurs while reading stream header
         * @throws SecurityException if untrusted subclass illegally overrides security-sensitive methods
         * @throws NullPointerException if <code>in</code> is <code>null</code>
         */
        public SecureObjectInputStream( InputStream in )
            throws IOException
        {
            super( in );

            this.inLoader = in.getClass().getClassLoader();
            this.serializableClasses = new ArrayList<>();
            this.permissions = new ArrayList<>();
            addDeserializePermissions( DeserializePermission.NONE, DeserializePermission.PRIMITIVES,
                DeserializePermission.ARRAY, DeserializePermission.PROXY );
        }

        /**
         * Add a permission to the allowed permissions list. Note that by adding the
         * {@linkplain DeserializePermission#NONE} permission, the entire list is
         * cleared first
         * 
         * @param permissions one or more permissions to add
         */
        public void addDeserializePermissions( IDeserializePermission... permissions )
        {
            if ( permissions == null )
            {
                return;
            }

            for ( int i = 0; i < permissions.length; i++ )
            {
                IDeserializePermission permission = permissions[i];

                if ( permission.equals( DeserializePermission.NONE ) )
                {
                    this.permissions.clear();
                }

                this.permissions.add( permission );
            }
        }

        /**
         * Explicitly allow the deserializer to deserialize the supplied Class(es).
         * This is a whitelist, so any classes not covered by the permissions or
         * this explicit class allowance will not be deserialized and the whole
         * deserialization action will fail
         * 
         * @param classes one of more classes to explicitly permit
         */
        public void addAllowedClasses( Class<?>... classes )
        {
            if ( classes == null )
            {
                return;
            }

            for ( int i = 0; i < classes.length; i++ )
            {
                this.serializableClasses.add( classes[i] );
            }
        }

        /**
         * Clears the list of all allowed classes
         */
        public void clearAllowedClasses()
        {
            this.serializableClasses.clear();
        }

        /////////////////////
        // Override Functions
        /////////////////////

        @Override
        protected Class<?> resolveClass( ObjectStreamClass classDesc )
            throws IOException, ClassNotFoundException
        {
            Class<?> clazz = load( classDesc.getName() );
            checkSecurity( clazz );
            return clazz;
        }

        @Override
        protected Class<?> resolveProxyClass( String[] interfaces )
            throws IOException, ClassNotFoundException
        {
            Class<?>[] cinterfaces = new Class[interfaces.length];
            for ( int i = 0; i < interfaces.length; i++ )
            {
                cinterfaces[i] = load( interfaces[i] );
            }

            // attempt to get a proxy class via the current thread's classloader
            Class<?> clazz = getProxyClassQuietly( cinterfaces );

            // couldn't find one, so fail
            if ( clazz == null )
            {
                throw new ClassNotFoundException(
                    "No classloader could be found to resolve proxy class interfaces: " + Arrays.toString( cinterfaces ) );
            }

            // check that a proxy class is allowed
            checkSecurity( clazz );
            return clazz;
        }

        /////////////////////
        // Internal Functions
        /////////////////////

        /**
         * Attempt to return a Class object that can act as a Proxy for the supplied
         * class interfaces. This will not throw any exceptions.
         * 
         * @param classInterfaces an array of interfaces to search against
         * @return a Proxy class that can be used with the supplied interfaces, or
         *         null if any error occurs
         */
        private Class<?> getProxyClassQuietly( Class<?>[] classInterfaces )
        {
            ClassLoader[] cl =
                new ClassLoader[] { Thread.currentThread().getContextClassLoader(), inLoader, FALLBACK_CLASS_LOADER };

            for ( ClassLoader loader : cl )
            {
                try
                {
                    return Proxy.getProxyClass( loader, classInterfaces );
                }
                catch ( Exception e )
                {
                    // ignore
                }
            }
            return null;
        }

        /**
         * The core security section, checkSecurity examines the Permissions
         * whitelist and the AllowedClasses whitelist to see if the supplied class
         * is permitted by any of the allowances. If neither list allows this class,
         * throw an exception
         * 
         * @param clazz check this class for permission to deserialize
         * @throws ClassNotFoundException if this class is not permitted to be
         *             deserialized
         */
        private void checkSecurity( Class<?> clazz )
            throws ClassNotFoundException
        {

            for ( IDeserializePermission permission : this.permissions )
            {
                if ( permission.allowed( clazz ) )
                {
                    return; // general permission allows this class
                }
            }

            for ( Class<?> allowedClazz : this.serializableClasses )
            {
                if ( allowedClazz.equals( clazz ) )
                {
                    return; // specific class permission allows this class
                }
            }

            // class is not permitted
            throw new ClassNotFoundException( "Forbidden " + clazz + ". This class is not allowed to be deserialized." );
        }

        /**
         * Attempt to load a Class by name from one or more classloaders. This uses
         * a fallback method to check against multiple classloaders before
         * attempting to load from the current context's classloader
         * 
         * @param className a String of the fully qualified Class name
         * @return a class if it could be loaded, or null
         */
        private Class<?> load( String className )
            throws ClassNotFoundException
        {
            ClassLoader[] cl =
                new ClassLoader[] { Thread.currentThread().getContextClassLoader(), inLoader, FALLBACK_CLASS_LOADER };

            // try the different class loaders
            for ( ClassLoader loader : cl )
            {
                try
                {
                    return Class.forName( className, false, loader );
                }
                catch ( ClassNotFoundException e )
                {
                    // ignore
                }
            }
            throw new ClassNotFoundException( "No classloader could load the class " + className );
        }

    }
