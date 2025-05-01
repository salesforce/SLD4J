package com.salesforce.sld.xml.sax;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HandlerMock
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
