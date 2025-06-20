package com.salesforce.sld.parser.xml;

/**
 * 
 * @author cyril.mathew
 *
 */

public enum AttackState {
	
	SAFE, FILE, HTTPINTERNAL, HTTPEXTERNAL, LAUGHS;
    public boolean isState( AttackState... attackStates )
    {
        for ( AttackState state : attackStates )
        {
            if ( this.equals( state ) )
            {
                return true;
            }
        }
        return false;
    }

}
