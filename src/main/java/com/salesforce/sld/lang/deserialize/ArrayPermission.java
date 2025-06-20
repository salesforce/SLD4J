package com.salesforce.sld.lang.deserialize;

import com.salesforce.sld.lang.SecureObjectInputStream;

/**
 * This {@linkplain IDeserializePermission} allows any class that is an Array. The permission does not check if the
 * class that makes up the Array is allowed. That is covered by
 * {@linkplain SecureObjectInputStream#addAllowedClasses(Class...)} calls.
 * 
 * @author csmith
 */
class ArrayPermission
    implements IDeserializePermission
{
    @Override
    public boolean allowed( Class<?> type )
    {
        return type != null && type.isArray();
    }
}
