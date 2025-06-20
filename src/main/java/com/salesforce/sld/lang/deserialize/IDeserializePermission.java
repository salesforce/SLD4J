package com.salesforce.sld.lang.deserialize;

/**
 * INterface descriptor for the Permissions governing wide sections of deserialization.
 * 
 * @author csmith
 */
public interface IDeserializePermission
{
    /**
     * Used to denote whether the supplied class is allowed by this {@linkplain DeserializePermission} or not
     * 
     * @param type a Class to permit or not
     * @return true if this permission allows the class to be deserialized, and false if not.
     */
    public boolean allowed( Class<?> type );
}
