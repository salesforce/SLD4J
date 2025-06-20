package com.salesforce.sld.parser.xml.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.salesforce.sld.SilentReporterWrapper;

public class SecureXMLInputFactoryTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testSetPropertyException()
    {
        exception.expect( IllegalArgumentException.class );

        SecureXMLInputFactory xif = SecureXMLInputFactory.newFactory();

        xif.setProperty( SecureXMLInputFactory.SUPPORT_DTD, true );
    }

    @Test
    public void testSetPropertyOK()
    {
        SecureXMLInputFactory xif = SecureXMLInputFactory.newFactory();

        xif.setProperty( "javax.xml.stream.isNamespaceAware", false );
    }

    @Test
    public void testPropertySupport()
    {
        SecureXMLInputFactory xif = SecureXMLInputFactory.newFactory();

        assertFalse( xif.isPropertySupported( SecureXMLInputFactory.SUPPORT_DTD ) );
        assertTrue( xif.isPropertySupported( "javax.xml.stream.isNamespaceAware" ) );
    }

    @Test
    public void testUntouched()
        throws IOException, XMLStreamException, TransformerConfigurationException, TransformerFactoryConfigurationError,
        TransformerException
    {
        SecureXMLInputFactory xif1 = SecureXMLInputFactory.newFactory();
        SecureXMLInputFactory xif2 = SecureXMLInputFactory.newInstance();

        SecureXMLInputFactory[] xifs = { xif1, xif2 };

        String xmlInput = "<?xml version=\"1.0\"?> \n<foo>normalXML</foo>";

        String systemId = "foo";
        String propertyName = "javax.xml.stream.isNamespaceAware";
        Object propertyValue = Boolean.TRUE;
        String xmlBase;
        String xmlTest;

        String xmlStreamStr;
        String xmlEventStr;

        for ( SecureXMLInputFactory xif : xifs )
        {
            XMLStreamReader xmlsr = xif.createXMLStreamReader( new StringReader( xmlInput ) );
            xmlBase = getXml( xmlsr );
            xmlStreamStr = xmlBase;

            xmlTest = getXml( xif.createXMLStreamReader( new StreamSource( new StringReader( xmlInput ) ) ) );
            assertEquals( xmlBase, xmlTest );

            xmlTest = getXml( xif.createXMLStreamReader( IOUtils.toInputStream( xmlInput, "UTF-8" ) ) );
            assertEquals( xmlBase, xmlTest );

            xmlTest = getXml( xif.createXMLStreamReader( IOUtils.toInputStream( xmlInput, "UTF-8" ), "UTF-8" ) );
            assertEquals( xmlBase, xmlTest );

            xmlTest = getXml( xif.createXMLStreamReader( systemId, IOUtils.toInputStream( xmlInput, "UTF-8" ) ) );
            assertEquals( xmlBase, xmlTest );

            xmlTest = getXml( xif.createXMLStreamReader( systemId, new StringReader( xmlInput ) ) );
            assertEquals( xmlBase, xmlTest );

            XMLEventReader xmler = xif.createXMLEventReader( new StringReader( xmlInput ) );
            xmlBase = getXml( xmler );
            xmlEventStr = xmlBase;

            xmlTest = getXml( xif.createXMLEventReader( systemId, new StringReader( xmlInput ) ) );
            assertEquals( xmlBase, xmlTest );

            xmlTest = getXml( xif.createXMLEventReader( xif.createXMLStreamReader( new StringReader( xmlInput ) ) ) );
            assertEquals( xmlBase, xmlTest );

            xmlTest = getXml( xif.createXMLEventReader( new StreamSource( new StringReader( xmlInput ) ) ) );
            assertEquals( xmlBase, xmlTest );

            xmlTest = getXml( xif.createXMLEventReader( IOUtils.toInputStream( xmlInput, "UTF-8" ) ) );
            assertEquals( xmlBase, xmlTest );

            xmlTest = getXml( xif.createXMLEventReader( IOUtils.toInputStream( xmlInput, "UTF-8" ), "UTF-8" ) );
            assertEquals( xmlBase, xmlTest );

            xmlTest = getXml( xif.createXMLEventReader( systemId, IOUtils.toInputStream( xmlInput, "UTF-8" ) ) );
            assertEquals( xmlBase, xmlTest );

            xmlTest = getXml( xif.createFilteredReader( xif.createXMLStreamReader( new StringReader( xmlInput ) ),
                new SilentReporterWrapper() ) );

            assertEquals( xmlStreamStr, xmlTest );

            xmlTest = getXml( xif.createFilteredReader( xif.createXMLEventReader( new StringReader( xmlInput ) ),
                new SilentReporterWrapper() ) );

            assertEquals( xmlEventStr, xmlTest );

            SilentReporterWrapper se1 = new SilentReporterWrapper();
            xif.setXMLResolver( se1 );
            assertEquals( se1, xif.getXMLResolver() );

            SilentReporterWrapper se2 = new SilentReporterWrapper();
            xif.setXMLReporter( se2 );
            assertEquals( se2, xif.getXMLReporter() );

            xif.setProperty( propertyName, propertyValue );
            assertEquals( propertyValue, xif.getProperty( propertyName ) );
            assertTrue( xif.isPropertySupported( propertyName ) );

            SilentReporterWrapper tseaf = new SilentReporterWrapper();
            xif.setEventAllocator( tseaf );
            assertEquals( tseaf, xif.getEventAllocator() );

        }
    }

    private String getXml( XMLStreamReader xmlr )
        throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter stringWriter = new StringWriter();
        transformer.transform( new StAXSource( xmlr ), new StreamResult( stringWriter ) );
        return stringWriter.toString();
    }

    private String getXml( XMLEventReader xmlr )
        throws XMLStreamException
    {
        while ( xmlr.hasNext() )
        {
            XMLEvent event = (XMLEvent) xmlr.next();
            if ( event.isCharacters() )
            {
                Characters characters = (Characters) event;
                return characters.getData();
            }
        }
        return null;
    }
}
