package com.salesforce.sld.lang;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PassthroughInvocationHandler
    implements InvocationHandler, Serializable
{

    private static final long serialVersionUID = 0L;

    private final Object target;

    public PassthroughInvocationHandler( Object target )
    {
        this.target = target;
    }

    public static Object newInstance( Object obj )
    {
        return Proxy.newProxyInstance( obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
            new PassthroughInvocationHandler( obj ) );
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args )
        throws Throwable
    {
        return method.invoke( target, args );
    }
}
