package com.salesforce.sld.lang.deserialize;

import com.salesforce.sld.lang.SecureObjectInputStream;

/**
 * Disallows all classes. This {@linkplain IDeserializePermission} is a placeholder and a default configuration. It is
 * used in {@linkplain SecureObjectInputStream} to clear the permissions list.
 * 
 * @author csmith
 */
class NoPermission
    implements IDeserializePermission
{
    @Override
    public boolean allowed( Class<?> type )
    {
        return false;
    }
}
