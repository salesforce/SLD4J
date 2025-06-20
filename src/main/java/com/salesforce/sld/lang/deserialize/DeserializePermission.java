package com.salesforce.sld.lang.deserialize;

/**
 * A convenience enumeration, the {@linkplain DeserializePermission} organizes the other
 * {@linkplain IDeserializePermission} objects.
 * 
 * @author csmith
 */
public enum DeserializePermission
    implements IDeserializePermission
{
    //@formatter:off
    /**
     * Holds the {@linkplain NoPermission}
     */
    NONE( new NoPermission() ), 
    /**
     * Holds the {@linkplain PrimitivePermission}
     */
    PRIMITIVES( new PrimitivePermission() ),
    /**
     * Holds the {@linkplain ArrayPermission}
     */
    ARRAY( new ArrayPermission() ),
    /**
     * Holds the {@linkplain ProxyPermission}
     */
    PROXY( new ProxyPermission() )
    // All - This doesn't exist, and shouldn't 
    ;
    //@formatter:on

    private final IDeserializePermission permission;

    private DeserializePermission( IDeserializePermission permission )
    {
        this.permission = permission;
    }

    @Override
    public boolean allowed( Class<?> type )
    {
        return this.permission.allowed( type );
    }

}
