package com.salesforce.sld.parser.xml.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class XMLResolutionExceptionTest
{

    @Test
    public void testInternalAddress()
    {
        String uri = "http://192.168.1.1";
        try
        {
            throw new XMLResolutionException( getClass(), AttackType.INTERNALADDRESS, uri );
        }
        catch ( XMLResolutionException e )
        {
            assertEquals( getClass().getSimpleName(), e.getThrowClass() );
            assertEquals( AttackType.INTERNALADDRESS, e.getAttackType() );
            assertThat( e.getErrorMessage(), CoreMatchers.allOf( CoreMatchers.containsString( e.getAttackType().name() ),
                CoreMatchers.containsString( uri ) ) );
        }
    }

    @Test
    public void testFile()
    {
        String uri = "file:///etc/passwd";
        try
        {
            throw new XMLResolutionException( getClass(), AttackType.FILE, uri );
        }
        catch ( XMLResolutionException e )
        {
            assertEquals( getClass().getSimpleName(), e.getThrowClass() );
            assertEquals( AttackType.valueOf( "FILE" ), e.getAttackType() );
            assertThat( e.getErrorMessage(), CoreMatchers.allOf( CoreMatchers.containsString( e.getAttackType().name() ),
                CoreMatchers.containsString( uri ) ) );
        }
    }

    @Test
    public void testArbitraryAttack()
    {
        String uri = "file:///etc/passwd";
        AttackType attack = AttackType.FILE;
        try
        {
            throw new XMLResolutionException( getClass(), attack, uri );
        }
        catch ( XMLResolutionException e )
        {
            assertEquals( getClass().getSimpleName(), e.getThrowClass() );
            assertEquals( attack, e.getAttackType() );
            assertThat( e.getErrorMessage(),
                CoreMatchers.allOf( CoreMatchers.containsString( attack.name() ), CoreMatchers.containsString( uri ) ) );
        }

    }

}
