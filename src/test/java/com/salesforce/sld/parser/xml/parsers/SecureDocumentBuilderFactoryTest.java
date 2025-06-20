package com.salesforce.sld.parser.xml.parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;
import com.salesforce.sld.SilentReporterWrapper;
import com.salesforce.sld.parser.xml.AttackState;
import com.salesforce.sld.parser.xml.validation.SecureSchemaFactory;

@RunWith( Parameterized.class )
public class SecureDocumentBuilderFactoryTest
{
	
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    public static final String FILEXML = "/xml/file.xml";

    public static final String INTERNALXML = "/xml/http_internal.xml";

    public static final String EXTERNALXML = "/xml/http_external.xml";

    public static final String LAUGHXML = "/xml/billion_laughs.xml";

    private static final String NORMALXML = "normalXML";

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
    public void testNewInstance()
            throws ParserConfigurationException, SAXException, IOException
    {
    	if ( attackState.isState( AttackState.FILE, AttackState.HTTPINTERNAL, AttackState.HTTPEXTERNAL,
                AttackState.LAUGHS ) )
            {
                exception.expect( SAXException.class );
            }

            DocumentBuilderFactory dbf = SecureDocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            SilentReporterWrapper se = new SilentReporterWrapper();
            db.setErrorHandler( se );
            db.parse( IOUtils.toInputStream( this.attack, "UTF-8" ) );
    }

    @Test
    public void testXIncludeException()
    {
        exception.expect( IllegalArgumentException.class );
        
        assertFalse( SecureDocumentBuilderFactory.newInstance().isXIncludeAware() );
        
        SecureDocumentBuilderFactory.newInstance().setXIncludeAware( true );
    }

    @Test
    public void testExpandEntityException()
    {
        exception.expect( IllegalArgumentException.class );
       
        assertFalse( SecureDocumentBuilderFactory.newInstance().isExpandEntityReferences() );

        SecureDocumentBuilderFactory.newInstance().setExpandEntityReferences( true );
    }

    @Test
    public void testSetFeatureOK()
        throws ParserConfigurationException
    {
        SecureDocumentBuilderFactory.newInstance().setFeature( "http://xml.org/sax/features/namespaces", true );
    }

    @Test
    public void testSetFeatureExcept()
        throws ParserConfigurationException
    {
        exception.expect( ParserConfigurationException.class );

        SecureDocumentBuilderFactory.newInstance().setFeature( "http://apache.org/xml/features/disallow-doctype-decl", false );
    }

    @Test
    public void testUntouchedFeatures()
        throws ParserConfigurationException, SAXException
    {
        SecureDocumentBuilderFactory dbf = SecureDocumentBuilderFactory.newInstance();
        SecureDocumentBuilderFactory.newInstance( "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl",
            getClass().getClassLoader() );

        dbf.setFeature( "http://xml.org/sax/features/namespaces", true );
        assertTrue( dbf.getFeature( "http://xml.org/sax/features/namespaces" ) );

        dbf.setAttribute( "http://apache.org/xml/features/dom/create-entity-ref-nodes", Boolean.TRUE );
        assertEquals( Boolean.TRUE, dbf.getAttribute( "http://apache.org/xml/features/dom/create-entity-ref-nodes" ) );

        dbf.setCoalescing( true );
        assertTrue( dbf.isCoalescing() );

        dbf.setIgnoringComments( true );
        assertTrue( dbf.isIgnoringComments() );

        dbf.setIgnoringElementContentWhitespace( true );
        assertTrue( dbf.isIgnoringElementContentWhitespace() );

        Schema schema = SecureSchemaFactory.newInstance().newSchema();
        dbf.setSchema( schema );
        assertEquals( schema, dbf.getSchema() );

        dbf.setValidating( true );
        assertTrue( dbf.isValidating() );

        dbf.setNamespaceAware( true );
        assertTrue( dbf.isNamespaceAware() );
    }
}
