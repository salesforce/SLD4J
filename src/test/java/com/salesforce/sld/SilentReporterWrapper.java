package com.salesforce.sld;

import java.io.IOException;

import javax.xml.stream.EventFilter;
import javax.xml.stream.Location;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SilentReporterWrapper
    implements ErrorHandler, XMLReporter, XMLResolver, ErrorListener, DTDHandler, EntityResolver, ContentHandler,
    LSResourceResolver, URIResolver, StreamFilter, EventFilter, XMLEventAllocator
{

    boolean encounteredWarning = false;

    boolean encounteredError = false;

    boolean encounteredFatal = false;

    String report = null;

    @Override
    public void warning( SAXParseException exception )
        throws SAXException
    {
        encounteredWarning = true;
    }

    @Override
    public void error( SAXParseException exception )
        throws SAXException
    {
        encounteredError = true;
    }

    @Override
    public void fatalError( SAXParseException exception )
        throws SAXException
    {
        encounteredFatal = true;
    }

    @Override
    public void report( String message, String errorType, Object relatedInformation, Location location )
        throws XMLStreamException
    {
        report = message;
    }

    @Override
    public Object resolveEntity( String publicID, String systemID, String baseURI, String namespace )
        throws XMLStreamException
    {
        return null;
    }

    @Override
    public void warning( TransformerException exception )
        throws TransformerException
    {
        encounteredWarning = true;
    }

    @Override
    public void error( TransformerException exception )
        throws TransformerException
    {
        encounteredError = true;
    }

    @Override
    public void fatalError( TransformerException exception )
        throws TransformerException
    {
        encounteredFatal = true;
    }

    @Override
    public void setDocumentLocator( Locator locator )
    {

    }

    @Override
    public void startDocument()
        throws SAXException
    {

    }

    @Override
    public void endDocument()
        throws SAXException
    {

    }

    @Override
    public void startPrefixMapping( String prefix, String uri )
        throws SAXException
    {

    }

    @Override
    public void endPrefixMapping( String prefix )
        throws SAXException
    {

    }

    @Override
    public void startElement( String uri, String localName, String qName, Attributes atts )
        throws SAXException
    {

    }

    @Override
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {

    }

    @Override
    public void characters( char[] ch, int start, int length )
        throws SAXException
    {

    }

    @Override
    public void ignorableWhitespace( char[] ch, int start, int length )
        throws SAXException
    {

    }

    @Override
    public void processingInstruction( String target, String data )
        throws SAXException
    {

    }

    @Override
    public void skippedEntity( String name )
        throws SAXException
    {

    }

    @Override
    public InputSource resolveEntity( String publicId, String systemId )
        throws SAXException, IOException
    {
        return null;
    }

    @Override
    public void notationDecl( String name, String publicId, String systemId )
        throws SAXException
    {

    }

    @Override
    public void unparsedEntityDecl( String name, String publicId, String systemId, String notationName )
        throws SAXException
    {

    }

    @Override
    public LSInput resolveResource( String type, String namespaceURI, String publicId, String systemId, String baseURI )
    {
        return null;
    }

    @Override
    public Source resolve( String href, String base )
        throws TransformerException
    {
        return null;
    }

    @Override
    public XMLEventAllocator newInstance()
    {
        return null;
    }

    @Override
    public XMLEvent allocate( XMLStreamReader reader )
        throws XMLStreamException
    {
        return null;
    }

    @Override
    public void allocate( XMLStreamReader reader, XMLEventConsumer consumer )
        throws XMLStreamException
    {

    }

    @Override
    public boolean accept( XMLEvent event )
    {
        return true;
    }

    @Override
    public boolean accept( XMLStreamReader reader )
    {
        return true;
    }
}
