package com.salesforce.sld.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import com.salesforce.sld.lang.deserialize.IDeserializePermission;

public class SerializeHelpers
{
    public static Object serDeserRoundtrip( Object initial )
        throws Exception
    {
        return serDeserRoundtrip( initial, null, null );
    }

    public static Object serDeserRoundtrip( Object initial, IDeserializePermission... permissions )
        throws Exception
    {
        return serDeserRoundtrip( initial, permissions, null );
    }

    public static Object serDeserRoundtrip( Object initial, Class<?>... allowedClasses )
        throws Exception
    {
        return serDeserRoundtrip( initial, null, allowedClasses );
    }

    public static Object serDeserRoundtrip( Object initial, IDeserializePermission[] permissions, Class<?>[] allowedClasses )
        throws Exception
    {
        byte[] ser = serialize( initial );
        ByteArrayInputStream bais = new ByteArrayInputStream( ser );
        SecureObjectInputStream sois = new SecureObjectInputStream( bais );
        if ( allowedClasses != null )
        {
            sois.addAllowedClasses( allowedClasses );
        }
        if ( permissions != null )
        {
            sois.addDeserializePermissions( permissions );
        }
        Object o = sois.readObject();
        sois.close();
        return o;
    }

    public static byte[] serialize( Object o )
        throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return baos.toByteArray();
    }
}
