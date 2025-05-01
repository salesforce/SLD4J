package com.salesforce.sld.lang;

import java.io.Serializable;
import java.util.Arrays;

public class OISMockClass
    implements IMockProxy, Serializable
{
    private static final long serialVersionUID = -5910181785976103513L;

    public String name;

    public int number;

    public String[] names;

    public Character firstChar;

    public OISMockClass( String name )
    {
        this.name = name;
        this.number = name.hashCode();
        this.firstChar = name.charAt( 0 );
        this.names = new String[] { this.name };
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public boolean equals( Object o )
    {
        boolean equal = false;
        if ( o instanceof OISMockClass )
        {
            OISMockClass that = (OISMockClass) o;
            equal = that.firstChar.equals( this.firstChar ) && that.name.equals( this.name )
                && Arrays.equals( that.names, this.names ) && that.number == this.number;
        }

        return equal;
    }
}
