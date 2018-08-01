package com.salesforce.sld.encoders;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SubclassingTest
{
    static class CaesarCipherManipulator
        extends AbstractCharacterManipulator
    {
        @Override
        protected String getCorrectCharacter( Character c )
        {
            char shift = (char) ( c + 13 );
            if ( shift > 'z' )
            {
                shift = (char) ( shift - 26 );
            }
            else if ( shift < 'a' )
            {
                shift = (char) ( shift + 26 );
            }

            return String.valueOf( shift );
        }
    }

    static class TestSecureEncoder
        extends SecureEncoder
    {
        TestSecureEncoder()
        {
            super();
        }

        public String encodeCaesar( String input )
        {
            return encode( new CaesarCipherManipulator(), input );
        }
    }

    @Test
    public void testSubclassing()
    {
        String testString = "foobar";
        String expected = "sbbone";
        TestSecureEncoder encoder = new TestSecureEncoder();
        String result = encoder.encodeCaesar( testString );

        assertEquals( expected, result );
        assertEquals( testString, encoder.encodeCaesar( result ) );

        String standardResult = encoder.encodeHtmlContent( testString );
        assertEquals( testString, standardResult );
    }
    
}
