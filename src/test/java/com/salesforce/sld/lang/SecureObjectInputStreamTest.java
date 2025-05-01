package com.salesforce.sld.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import com.salesforce.sld.lang.deserialize.IDeserializePermission;

public class SecureObjectInputStreamTest
{
    private String name = "Chris Smith";

    @Test
    public void happyPath()
        throws Exception
    {
        OISMockClass mock = new OISMockClass( name );
        OISMockClass deser = (OISMockClass) SerializeHelpers.serDeserRoundtrip( mock, OISMockClass.class );

        assertEquals( mock, deser );
    }

    @Test
    public void happyPathDifferentObject()
        throws Exception
    {
        OISMockClass mock = new OISMockClass( name );
        OISMockClass mock2 = new OISMockClass( "Other" );
        OISMockClass deser = (OISMockClass) SerializeHelpers.serDeserRoundtrip( mock, OISMockClass.class );

        assertEquals( mock, deser );
        assertFalse( mock2.equals( deser ) );
    }

    @Test( expected = ClassNotFoundException.class )
    public void disallowedArray()
        throws Exception
    {
        OISMockClass[] mock = new OISMockClass[] { new OISMockClass( name ) };
        SerializeHelpers.serDeserRoundtrip( mock );
    }

    @Test( expected = ClassNotFoundException.class )
    public void disallowedClass()
        throws Exception
    {
        OISMockClass mock = new OISMockClass( name );
        SerializeHelpers.serDeserRoundtrip( mock );
    }

    @Test
    public void happyPathProxy()
        throws Exception
    {
        OISMockClass mClass = new OISMockClass( name );
        IMockProxy mock = (IMockProxy) PassthroughInvocationHandler.newInstance( mClass );
        SerializeHelpers.serDeserRoundtrip( mock, PassthroughInvocationHandler.class, OISMockClass.class );
    }

    @Test( expected = ClassNotFoundException.class )
    public void disallowedProxy()
        throws Exception
    {
        OISMockClass mClass = new OISMockClass( name );
        IMockProxy mock = (IMockProxy) PassthroughInvocationHandler.newInstance( mClass );
        SerializeHelpers.serDeserRoundtrip( mock, PassthroughInvocationHandler.class );
    }

    @Test( expected = ClassNotFoundException.class )
    public void addNullClassAllowed()
        throws Exception
    {
        OISMockClass mock = new OISMockClass( name );
        Class<?>[] allowed = null;

        byte[] ser = SerializeHelpers.serialize( mock );
        ByteArrayInputStream bais = new ByteArrayInputStream( ser );
        SecureObjectInputStream sois = new SecureObjectInputStream( bais );
        sois.addAllowedClasses( allowed );
        sois.readObject();
        sois.close();
    }

    @Test( expected = ClassNotFoundException.class )
    public void addNullPermission()
        throws Exception
    {
        OISMockClass mock = new OISMockClass( name );
        IDeserializePermission[] allowed = null;

        byte[] ser = SerializeHelpers.serialize( mock );
        ByteArrayInputStream bais = new ByteArrayInputStream( ser );
        SecureObjectInputStream sois = new SecureObjectInputStream( bais );
        sois.addDeserializePermissions( allowed );
        sois.readObject();
        sois.close();
    }

    @Test( expected = ClassNotFoundException.class )
    public void testClassClearing()
        throws Exception
    {
        OISMockClass mock = new OISMockClass( name );

        byte[] ser = SerializeHelpers.serialize( mock );
        ByteArrayInputStream bais = new ByteArrayInputStream( ser );
        SecureObjectInputStream sois = new SecureObjectInputStream( bais );
        sois.addAllowedClasses( OISMockClass.class );
        sois.clearAllowedClasses();
        sois.readObject();
        sois.close();
    }

    @Test( expected = ClassNotFoundException.class )
    public void testFromStream()
        throws Exception
    {
        byte[] ser = Base64.getDecoder().decode( IOUtils
            .toString( SecureObjectInputStreamTest.class.getResourceAsStream( "/deserialize/objectfile.ser" ), Charset.defaultCharset() ).trim() );

        ByteArrayInputStream bais = new ByteArrayInputStream( ser );
        SecureObjectInputStream sois = new SecureObjectInputStream( bais );
        sois.readObject();
        sois.close();
    }

    @Test
    public void loadFakeProxy()
        throws Exception
    {
        OISMockClass mock = new OISMockClass( name );

        byte[] ser = SerializeHelpers.serialize( mock );
        ByteArrayInputStream bais = new ByteArrayInputStream( ser );
        SecureObjectInputStream sois = new SecureObjectInputStream( bais );
        try
        {
            sois.resolveProxyClass( new String[] { Serializable.class.getName(), OISMockClass.class.getName() } );
            fail( "Should have thrown an exception" );
        }
        catch ( ClassNotFoundException e )
        {
            assertTrue( e.getMessage().contains( "No classloader could be found to resolve proxy class" ) );
        }
        finally
        {
            sois.close();
        }

    }

    @Test
    public void loadFakeClass()
        throws Exception
    {
        OISMockClass mock = new OISMockClass( name );

        byte[] ser = SerializeHelpers.serialize( mock );
        ByteArrayInputStream bais = new ByteArrayInputStream( ser );
        SecureObjectInputStream sois = new SecureObjectInputStream( bais );
        try
        {
            sois.resolveProxyClass( new String[] { "java.method.foobar", OISMockClass.class.getName() } );
            fail( "Should have thrown an exception" );
        }
        catch ( ClassNotFoundException e )
        {
            assertTrue( e.getMessage().contains( "java.method.foobar" ) );
        }
        finally
        {
            sois.close();
        }
    }
}
