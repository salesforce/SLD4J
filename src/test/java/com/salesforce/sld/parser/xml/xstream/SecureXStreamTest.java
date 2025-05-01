package com.salesforce.sld.parser.xml.xstream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.beans.EventHandler;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.salesforce.sld.foundation.exception.SecurityControlException;
import com.salesforce.sld.foundation.exception.SecurityControlRuntimeException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.NullConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.security.ForbiddenClassException;
import com.thoughtworks.xstream.security.NoPermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import com.thoughtworks.xstream.security.TypeHierarchyPermission;

public class SecureXStreamTest
{
    @Test
    public void testAllowedPermissions()
    {
        SecureXStream xstream = new SecureXStream();
        xstream.addPermission( new PrimitiveTypePermission() );
        xstream.addPermission( new NoTypePermission() );
        xstream.addPermission( new NullPermission() );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testDeniedPermission()
    {
        XStream xstream = new SecureXStream();
        xstream.denyPermission( new NoPermission( new TypeHierarchyPermission( InvocationHandler.class ) ) );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testInvalidExplicit()
    {
        SecureXStream xstream = new SecureXStream();
        Class<?>[] cls = { InvocationHandler.class };
        xstream.allowTypes( cls );
    }

    @Test
    public void testValidExplicit()
    {
        SecureXStream xstream = new SecureXStream();
        Class<?>[] cls = { SecurityControlRuntimeException.class };
        xstream.allowTypes( cls );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testInvalidNameExplicit()
    {
        SecureXStream xstream = new SecureXStream();
        String[] cls = { "SecurityControlRuntimeException" };
        xstream.allowTypes( cls );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testInvalidHierarchy()
    {
        SecureXStream xstream = new SecureXStream();
        xstream.allowTypeHierarchy( EventHandler.class );

    }

    @Test
    public void testAllowStringNull()
    {
        XStream xstream = new SecureXStream();
        String[] cls = null;
        xstream.allowTypes( cls );
    }

    @Test
    public void testAllowClassNull()
    {
        XStream xstream = new SecureXStream();
        Class<?>[] cls = null;
        xstream.allowTypes( cls );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testRegisterConverter()
    {
        SecureXStream xstream = new SecureXStream();
        xstream.registerConverter( new ConverterMock() );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testRegisterConverterPriority()
    {
        SecureXStream xstream = new SecureXStream();
        xstream.registerConverter( new ConverterMock(), 1 );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testInvalidRegisterLocalConverter()
    {
        SecureXStream xstream = new SecureXStream();
        xstream.registerLocalConverter( ConverterMock.class, "test", new ConverterMock() );
    }

    @Test
    public void testValidRegisterLocalConverter()
    {
        SecureXStream xstream = new SecureXStream();
        xstream.registerLocalConverter( NullConverter.class, "test", new NullConverter() );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testRegisterSingleValueConverterPriority()
    {
        SecureXStream xstream = new SecureXStream();
        xstream.registerConverter( new SingleValueConverterMock(), 1 );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testWildcard()
    {
        SecureXStream xstream = new SecureXStream();
        String[] wilds = { "java.lang.*" };
        xstream.allowTypesByWildcard( wilds );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testStringRegex()
    {
        SecureXStream xstream = new SecureXStream();
        String[] patts = { "^[ \t]+" };
        xstream.allowTypesByRegExp( patts );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testPatternRegex()
    {
        SecureXStream xstream = new SecureXStream();
        Pattern[] patts = { Pattern.compile( "^[ \t]+" ) };
        xstream.allowTypesByRegExp( patts );
    }

    @Test
    public void testValidHierarchy()
    {
        SecureXStream xstream = new SecureXStream();
        xstream.allowTypeHierarchy( SecurityControlException.class );
    }

    @Test
    public void testHierarchialConstructor()
    {
        SecureXStream xstream = new SecureXStream( (HierarchicalStreamDriver) null );
        xstream.allowTypeHierarchy( SecurityControlException.class );
    }

    @Test
    public void testReflectionProviderConstructor()
    {
        SecureXStream xstream = new SecureXStream( (ReflectionProvider) null );
        xstream.allowTypeHierarchy( SecurityControlException.class );
    }

    @Test
    public void testReflectionProviderHierarchialConstructor()
    {
        SecureXStream xstream = new SecureXStream( (ReflectionProvider) null, ( (HierarchicalStreamDriver) null ) );
        xstream.allowTypeHierarchy( SecurityControlException.class );
    }

    @Test
    public void testReflectionProviderStreamConstructor()
    {
        XStream xstream = new SecureXStream( (ReflectionProvider) null, (HierarchicalStreamDriver) null,
            new ClassLoaderReference( ClassLoader.getSystemClassLoader() ) );
        xstream.allowTypeHierarchy( SecurityControlException.class );
    }

    @Test
    public void testReflectionProviderStreamLoaderConstructor()
    {
        XStream xstream = new SecureXStream( (ReflectionProvider) null, (HierarchicalStreamDriver) null,
            ClassLoader.getSystemClassLoader() );
        xstream.allowTypeHierarchy( SecurityControlException.class );
    }

    @Test( expected = ForbiddenClassException.class )
    public void testMaliciousXML()
        throws IOException
    {
        SecureXStream xstream = new SecureXStream();
        xstream.allowTypes( new Class[] { ClassMock.class } );
        String xml = IOUtils.toString( SecureXStreamTest.class.getResourceAsStream( "/xml/xstream_handler.xml" ) );
        ClassMock exc = (ClassMock) xstream.fromXML( xml );
    }

    @Test
    public void testUnmarshallXML()
    {
        SecureXStream xstream = new SecureXStream();
        ClassMock test = new ClassMock( "testing", 1 );
        String xml = xstream.toXML( test );
        xstream.allowTypes( new Class[] { ClassMock.class } );
        ClassMock unmarshalled = (ClassMock) xstream.fromXML( xml );
        assertEquals( test.getID(), unmarshalled.getID() );
        assertEquals( test.getName(), unmarshalled.getName() );
        assertEquals( test.getArray(), unmarshalled.getArray() );
    }

    @Test
    public void testDifferentXMLObjects()
    {
        SecureXStream xstream = new SecureXStream();
        ClassMock valid = new ClassMock( "valid", 1 );
        String xml = xstream.toXML( valid );
        xstream.allowTypes( new Class[] { ClassMock.class } );
        ClassMock unmarshalled = (ClassMock) xstream.fromXML( xml );
        ClassMock invalid = new ClassMock( "invalid", 2 );
        assertNotEquals( invalid.getID(), unmarshalled.getID() );
        assertNotEquals( invalid.getName(), unmarshalled.getName() );
        assertNotEquals( invalid.getArray(), unmarshalled.getArray() );
    }

    @Test
    public void testAliasXMLObjects()
    {
        SecureXStream xstream = new SecureXStream();
        AliasClassMock valid = new AliasClassMock( "valid", 1 );
        xstream.alias( "aliasmock", AliasClassMock.class );
        xstream.alias( "mock", ClassMock.class );
        String xml = xstream.toXML( valid );
        xstream.allowTypes( new Class[] { ClassMock.class, AliasClassMock.class } );
        AliasClassMock unmarshalled = (AliasClassMock) xstream.fromXML( xml );
        assertEquals( valid.getMock().getID(), unmarshalled.getMock().getID() );
        assertEquals( valid.getMock().getName(), unmarshalled.getMock().getName() );
        assertEquals( valid.getMock().getArray(), unmarshalled.getMock().getArray() );
        assertEquals( valid.getMock().getID(), unmarshalled.getMock().getID() );
        assertEquals( valid.getMock().getName(), unmarshalled.getMock().getName() );
        assertEquals( valid.getMock().getArray(), unmarshalled.getMock().getArray() );
    }

    @Test( expected = SecurityControlRuntimeException.class )
    public void testObjectHierarchy()
    {
        SecureXStream xstream = new SecureXStream();
        xstream.allowTypeHierarchy( Object.class );
    }

}