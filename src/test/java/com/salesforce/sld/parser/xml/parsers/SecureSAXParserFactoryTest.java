package com.salesforce.sld.parser.xml.parsers;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.validation.Schema;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.salesforce.sld.parser.xml.AttackState;
import com.salesforce.sld.parser.xml.validation.SecureSchemaFactory;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith( Parameterized.class )
public class SecureSAXParserFactoryTest
{
    private class HandlerMock
            extends DefaultHandler
    {

        private String characters = "";

        public String getCharacters()
        {
            return this.characters;
        }

        @Override
        public void characters( char ch[], int start, int length )
                throws SAXException
        {
            this.characters = new String( ch );
        }

    }

    public static final String FILEXML = "/xml/file.xml";

    public static final String INTERNALXML = "/xml/http_internal.xml";

    public static final String EXTERNALXML = "/xml/http_external.xml";

    public static final String LAUGHXML = "/xml/billion_laughs.xml";

    private static final String NORMALXML = "normalXML";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Parameterized.Parameters( name = "{0}" )
    public static List<Object[]> attacks()
            throws IOException
    {

        String exec = "<foo>&xxe;</foo>";

        String fileXXE = ( SecureSAXParserFactory.class.getResourceAsStream( FILEXML ) ).toString() + exec;
        String httpInternalXXE = ( SecureSAXParserFactory.class.getResourceAsStream( INTERNALXML ) ).toString() + exec;
        String httpExternalXXE = ( SecureSAXParserFactory.class.getResourceAsStream( EXTERNALXML ) ).toString() + exec;
        String billionLaughs = ( SecureSAXParserFactory.class.getResourceAsStream( LAUGHXML ) ).toString() + exec;

        String safeXML = "<?xml version=\"1.0\"?> \n<foo>" + NORMALXML + "</foo>";

        //@formatter:off
        return Arrays.asList( new Object[][] {
                { AttackState.SAFE, safeXML },
                { AttackState.FILE, fileXXE },
                { AttackState.HTTPINTERNAL, httpInternalXXE },
                { AttackState.HTTPEXTERNAL, httpExternalXXE },
                { AttackState.LAUGHS, billionLaughs }
        } );
        //@formatter:on
    }

    @Parameterized.Parameter( 0 )
    public AttackState attackState;

    @Parameterized.Parameter( 1 )
    public String attack;

    @Test
    public void testSetFeatureOK()
        throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException
    {
        SecureSAXParserFactory fact = SecureSAXParserFactory.newInstance();
        fact.setFeature( "http://xml.org/sax/features/validation", false );
        assertFalse( fact.getFeature( "http://xml.org/sax/features/validation" ) );

    }

    @Test
    public void testSetFeatureExcept()
        throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException
    {
        exception.expect( SAXNotSupportedException.class );

        SecureSAXParserFactory fact = SecureSAXParserFactory.newInstance();
        fact.setFeature( "http://xml.org/sax/features/external-general-entities", true );
    }

    @Test
    public void testUntouchedFeatures()
        throws SAXException
    {
        SecureSAXParserFactory fact = SecureSAXParserFactory.newInstance();
        Schema schema = SecureSchemaFactory.newInstance().newSchema();

        fact.setXIncludeAware( true );
        assertTrue( fact.isXIncludeAware() );

        fact.setSchema( schema );
        assertEquals( schema, fact.getSchema() );

        fact.setNamespaceAware( true );
        assertTrue( fact.isNamespaceAware() );

        fact.setValidating( false );
        assertFalse( fact.isValidating() );

        // shouldn't throw or anything
        SecureSAXParserFactory.newInstance( "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl",
            null );
        SecureSAXParserFactory.newInstance( "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl",
            getClass().getClassLoader() );
    }

    @Test
    public void testSecureSAXParseFactStrict()
            throws ParserConfigurationException, SAXException, IOException
    {
        // throws due to DOCTYPE decl error
        if ( !attackState.isState( AttackState.SAFE ) )
        {
            exception.expect( SAXParseException.class );
        }

        SecureSAXParserFactory spf = SecureSAXParserFactory.newInstance();
        SAXParser parse = spf.newStrictSAXParser();
        HandlerMock h = new HandlerMock();
        parse.parse( IOUtils.toInputStream( this.attack, "UTF-8" ), h );

        // only for safe XML
        assertThat( h.getCharacters(), CoreMatchers.containsString( NORMALXML ) );
    }
}
