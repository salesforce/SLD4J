package com.salesforce.sld.crypto.random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Test;

public class _SecureRandomManaged
{

    @Test
    public void testNoFailures()
    {
        SecureRandomManaged rand = new SecureRandomManaged();

        long streamSize = 15L;
        int origin = 1;
        int bound = 100;

        rand.nextBoolean();
        rand.nextBytes( new byte[bound] );
        rand.nextDouble();
        rand.nextFloat();
        rand.nextGaussian();
        rand.nextInt();
        rand.nextInt( bound );
        rand.nextLong();
        rand.doubles();
        rand.doubles( origin, bound );
        rand.doubles( streamSize );
        rand.doubles( streamSize, origin, bound );
        rand.ints();
        rand.ints( origin, bound );
        rand.ints( streamSize );
        rand.ints( streamSize, origin, bound );
        rand.longs();
        rand.longs( streamSize );
        rand.longs( origin, bound );
        rand.longs( streamSize, origin, bound );
    }

    @Test
    public void testReseed()
        throws Exception
    {
        SecureRandomManaged rand = new SecureRandomManaged();
        int countdownMax = 100;
        int countdownStop = 95;

        rand.setSeed( new byte[] { 0 } );
        int countdown = getCountdown( rand );
        assertEquals( countdownMax, countdown );

        for ( int i = 0; i < countdownStop; i++ )
        {
            rand.nextInt();
        }
        countdown = getCountdown( rand );
        assertEquals( countdownMax - countdownStop, countdown );

        for ( int i = 0; i < countdownStop; i++ )
        {
            rand.nextInt();
        }
        countdown = getCountdown( rand );
        assertTrue( countdownMax < countdown );
    }

    @Test
    public void testSeeds()
        throws Exception
    {
        SecureRandomManaged rand = new SecureRandomManaged();

        int countdown = getCountdown( rand );
        byte[] seed = rand.generateSeed( 15 );
        assertEquals( 1000000, countdown );

        rand.setSeed( seed );
        countdown = getCountdown( rand );
        assertEquals( 100, countdown );
    }

    private static int getCountdown( SecureRandomManaged rand )
        throws Exception
    {
        Field privateField = SecureRandomManaged.class.getDeclaredField( "countdown" );
        privateField.setAccessible( true );
        Integer countdown = (Integer) privateField.get( rand );
        return countdown.intValue();
    }

}