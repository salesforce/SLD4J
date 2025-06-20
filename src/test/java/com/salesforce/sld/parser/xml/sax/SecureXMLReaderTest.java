package com.salesforce.sld.parser.xml.sax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.salesforce.sld.SilentReporterWrapper;

@RunWith( Parameterized.class )
public class SecureXMLReaderTest
{

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final String allowedFeature = "http://xml.org/sax/features/validation";

    private static final String disallowFeature = "http://xml.org/sax/features/external-general-entities";

    private static final String classname = "com.sun.org.apache.xerces.internal.parsers.SAXParser";

    @Parameters( name = "{0}" )
    public static Object[][] readers()
        throws SAXException
    {
        XMLReader badReader = XMLReaderFactory.createXMLReader();
        badReader.setFeature( disallowFeature, false );
        return new Object[][] { 
            { "Plain", new SecureXMLReader() },
            { "Classname", new SecureXMLReader( classname ) },
            { "Wrapped", new SecureXMLReader( badReader ) }, 
            };
    }

    @Parameter( 0 )
    public String name;

    @Parameter( 1 )
    public SecureXMLReader reader;

    @Test
    public void testFeaturesOK()
        throws SAXException
    {
        reader.setFeature( allowedFeature, false );
        assertFalse( reader.getFeature( allowedFeature ) );
    }

    @Test
    public void testFeaturesException()
        throws SAXException
    {
        exception.expect( SAXNotSupportedException.class );

        reader.setFeature( disallowFeature, true );
    }

    @Test
    public void testUntouched()
        throws SAXException, IOException
    {
        SilentReporterWrapper se = new SilentReporterWrapper();

        reader.setProperty( "http://apache.org/xml/properties/locale", Locale.CANADA );
        assertEquals( Locale.CANADA, reader.getProperty( "http://apache.org/xml/properties/locale" ) );

        reader.setEntityResolver( se );
        assertEquals( se, reader.getEntityResolver() );

        reader.setDTDHandler( se );
        assertEquals( se, reader.getDTDHandler() );

        reader.setContentHandler( se );
        assertEquals( se, reader.getContentHandler() );

        reader.setErrorHandler( se );
        assertEquals( se, reader.getErrorHandler() );

        reader.parse( getClass().getResource( "/xml/safe.xml" ).toString() );
    }
}
