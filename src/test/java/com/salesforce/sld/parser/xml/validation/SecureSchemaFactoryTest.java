package com.salesforce.sld.parser.xml.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Locale;

import javax.xml.XMLConstants;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;

import com.salesforce.sld.SilentReporterWrapper;

public class SecureSchemaFactoryTest
{

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testPropertyOK()
        throws SAXException
    {
        SecureSchemaFactory fact = SecureSchemaFactory.newInstance();
        fact.setProperty( "http://apache.org/xml/properties/locale", Locale.CANADA );
        assertEquals( Locale.CANADA, fact.getProperty( "http://apache.org/xml/properties/locale" ) );
    }

    @Test
    public void testPropertyException()
        throws SAXException
    {
        exception.expect( SAXNotSupportedException.class );

        SecureSchemaFactory fact = SecureSchemaFactory.newInstance();
        fact.setProperty( XMLConstants.ACCESS_EXTERNAL_DTD, "True" );
    }

    @Test
    public void testUntouched()
        throws SAXException, IOException
    {
        SecureSchemaFactory fact = SecureSchemaFactory.newInstance();
        SecureSchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
        SecureSchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI,
            "com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory", getClass().getClassLoader() );

        fact.setFeature( "http://apache.org/xml/features/validate-annotations", true );
        assertTrue( fact.getFeature( "http://apache.org/xml/features/validate-annotations" ) );

        assertTrue( fact.isSchemaLanguageSupported( XMLConstants.W3C_XML_SCHEMA_NS_URI ) );

        SilentReporterWrapper se = new SilentReporterWrapper();
        fact.setErrorHandler( se );
        assertEquals( se, fact.getErrorHandler() );

        fact.setResourceResolver( se );
        assertEquals( se, fact.getResourceResolver() );

        fact.newSchema();

        fact.newSchema( getClass().getResource( "/xml/schema.xsd" ) );

    }

}
