/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.parser.xml.stream;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.stream.EventFilter;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;

/**
 * An abstract base for wrapped XMLInputFactories. Contains no special logic,
 * only all basic wrapper methods
 * 
 * @author csmith
 */
abstract class AbstractXMLInputFactoryWrapper
    extends XMLInputFactory
{
    protected final XMLInputFactory factory;

    protected AbstractXMLInputFactoryWrapper( String factoryId, ClassLoader classLoader )
    {
        this.factory = newIFWrapperInstance( factoryId, classLoader );
    }

    protected abstract void setupWrapper( XMLInputFactory factory );

    private XMLInputFactory newIFWrapperInstance( String factoryId, ClassLoader classLoader )
    {
        XMLInputFactory xIF;

        if ( factoryId != null )
        {
            xIF = XMLInputFactory.newFactory( factoryId, classLoader );
        }
        else
        {
            xIF = XMLInputFactory.newFactory();
        }

        setupWrapper( xIF );
        return xIF;
    }

    @Override
    public XMLStreamReader createXMLStreamReader( Reader reader )
        throws XMLStreamException
    {
        return this.factory.createXMLStreamReader( reader );
    }

    @Override
    public XMLStreamReader createXMLStreamReader( Source source )
        throws XMLStreamException
    {
        return this.factory.createXMLStreamReader( source );
    }

    @Override
    public XMLStreamReader createXMLStreamReader( InputStream stream )
        throws XMLStreamException
    {
        return this.factory.createXMLStreamReader( stream );
    }

    @Override
    public XMLStreamReader createXMLStreamReader( InputStream stream, String encoding )
        throws XMLStreamException
    {
        return this.factory.createXMLStreamReader( stream, encoding );
    }

    @Override
    public XMLStreamReader createXMLStreamReader( String systemId, InputStream stream )
        throws XMLStreamException
    {
        return this.factory.createXMLStreamReader( systemId, stream );
    }

    @Override
    public XMLStreamReader createXMLStreamReader( String systemId, Reader reader )
        throws XMLStreamException
    {
        return this.factory.createXMLStreamReader( systemId, reader );
    }

    @Override
    public XMLEventReader createXMLEventReader( Reader reader )
        throws XMLStreamException
    {
        return this.factory.createXMLEventReader( reader );
    }

    @Override
    public XMLEventReader createXMLEventReader( String systemId, Reader reader )
        throws XMLStreamException
    {
        return this.factory.createXMLEventReader( systemId, reader );
    }

    @Override
    public XMLEventReader createXMLEventReader( XMLStreamReader reader )
        throws XMLStreamException
    {
        return this.factory.createXMLEventReader( reader );
    }

    @Override
    public XMLEventReader createXMLEventReader( Source source )
        throws XMLStreamException
    {
        return this.factory.createXMLEventReader( source );
    }

    @Override
    public XMLEventReader createXMLEventReader( InputStream stream )
        throws XMLStreamException
    {
        return this.factory.createXMLEventReader( stream );
    }

    @Override
    public XMLEventReader createXMLEventReader( InputStream stream, String encoding )
        throws XMLStreamException
    {
        return this.factory.createXMLEventReader( stream, encoding );
    }

    @Override
    public XMLEventReader createXMLEventReader( String systemId, InputStream stream )
        throws XMLStreamException
    {
        return this.factory.createXMLEventReader( systemId, stream );
    }

    @Override
    public XMLStreamReader createFilteredReader( XMLStreamReader reader, StreamFilter filter )
        throws XMLStreamException
    {
        return this.factory.createFilteredReader( reader, filter );
    }

    @Override
    public XMLEventReader createFilteredReader( XMLEventReader reader, EventFilter filter )
        throws XMLStreamException
    {
        return this.factory.createFilteredReader( reader, filter );
    }

    @Override
    public XMLResolver getXMLResolver()
    {
        return this.factory.getXMLResolver();
    }

    @Override
    public void setXMLResolver( XMLResolver resolver )
    {
        this.factory.setXMLResolver( resolver );
    }

    @Override
    public XMLReporter getXMLReporter()
    {
        return this.factory.getXMLReporter();
    }

    @Override
    public void setXMLReporter( XMLReporter reporter )
    {
        this.factory.setXMLReporter( reporter );
    }

    @Override
    public void setProperty( String name, Object value )
        throws IllegalArgumentException
    {
        this.factory.setProperty( name, value );
    }

    @Override
    public Object getProperty( String name )
        throws IllegalArgumentException
    {
        return this.factory.getProperty( name );
    }

    @Override
    public boolean isPropertySupported( String name )
    {
        return this.factory.isPropertySupported( name );
    }

    @Override
    public void setEventAllocator( XMLEventAllocator allocator )
    {
        this.factory.setEventAllocator( allocator );
    }

    @Override
    public XMLEventAllocator getEventAllocator()
    {
        return this.factory.getEventAllocator();
    }
}
