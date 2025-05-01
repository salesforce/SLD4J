package com.salesforce.sld.parser.xml.transform.sax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.xml.XMLConstants;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

import com.salesforce.sld.SilentReporterWrapper;

public class SecureSAXTransformerFactoryTest
{

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testAttributeOK()
        throws SAXException
    {
        SecureSAXTransformerFactory fact = SecureSAXTransformerFactory.newInstance();
        fact.setAttribute( "enable-inlining", "" );

    }

    @Test
    public void testAttributeException()
        throws SAXException
    {
        exception.expect( IllegalArgumentException.class );

        SecureSAXTransformerFactory fact = SecureSAXTransformerFactory.newInstance();
        fact.setAttribute( XMLConstants.ACCESS_EXTERNAL_DTD, "True" );
    }

    @Test
    public void testUntouched()
        throws SAXException, IOException, TransformerConfigurationException
    {
        SecureSAXTransformerFactory fact = SecureSAXTransformerFactory.newInstance();
        SecureSAXTransformerFactory.newInstance(
            "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", getClass().getClassLoader() );

        Templates temp =
            fact.newTemplates( new StreamSource( getClass().getResourceAsStream( "/xml/stylesheet.xslt" ) ) );

        assertNotNull( fact.newTemplatesHandler() );

        assertNotNull( fact.newTransformerHandler() );
        assertNotNull( fact.newTransformerHandler( new StreamSource( getClass().getResourceAsStream( "/xml/stylesheet.xslt" ) ) ) );
        assertNotNull( fact.newTransformerHandler( temp ) );
        
        assertNotNull( fact.newXMLFilter( new StreamSource( getClass().getResourceAsStream( "/xml/stylesheet.xslt" ) ) ) );
        assertNotNull( fact.newXMLFilter( temp ) );
        
        assertNotNull( fact.newTransformer() );
        assertNotNull( fact.newTransformer( new StreamSource( getClass().getResourceAsStream( "/xml/stylesheet.xslt" ) ) ) );

        fact.getAssociatedStylesheet( new StreamSource( getClass().getResourceAsStream( "/xml/stylesheet.xslt" ) ), "",
            "", Charset.defaultCharset().name() );

        SilentReporterWrapper se = new SilentReporterWrapper();
        fact.setURIResolver( se );
        assertEquals(se, fact.getURIResolver() );
        
        fact.setErrorListener( se );
        assertEquals(se, fact.getErrorListener() );

        fact.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
        fact.getFeature( XMLConstants.FEATURE_SECURE_PROCESSING );
        fact.setAttribute( "enable-inlining", true );
        fact.getAttribute( "enable-inlining" );
    }

}
