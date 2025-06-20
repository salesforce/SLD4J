package com.salesforce.sld.parser.xml.xstream;

import com.thoughtworks.xstream.converters.SingleValueConverter;

public class SingleValueConverterMock implements SingleValueConverter
{

    @Override
    public boolean canConvert( Class type )
    {
        return false;
    }

    @Override
    public String toString( Object obj )
    {
        return null;
    }

    @Override
    public Object fromString( String str )
    {
        return null;
    }
    
}