package com.salesforce.sld.lang.deserialize;

import java.lang.reflect.Proxy;

/**
 * Deserialization can use the proxy interface to build dynamic proxies for subclasses. This permission allows that
 * feature. Note that the Proxy interfaces defined by the Proxy as well as the proxied subclass are all checked
 * separately. This only allows Proxies to be used.
 * 
 * @author csmith
 */
class ProxyPermission
    implements IDeserializePermission
{

    @Override
    public boolean allowed( Class<?> type )
    {
        return type != null && Proxy.class.isAssignableFrom( type );
    }

}
