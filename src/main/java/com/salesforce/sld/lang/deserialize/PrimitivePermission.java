package com.salesforce.sld.lang.deserialize;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This {@linkplain IDeserializePermission} allows all primitive java types as well as their boxed equivalents. This
 * includes the Boolean, Character, Byte, Short, Integer, Long, Float, Double, Void, and String types.
 * 
 * @author csmith
 */
class PrimitivePermission
    implements IDeserializePermission
{
    /**
     * static set of Boxed Primitives
     */
    //@formatter:off
    private static final Set<Class<?>> BOXED_PRIMITIVES = new HashSet<>( Arrays.asList(
        Boolean.class, 
        Character.class, 
        Byte.class, 
        Short.class, 
        Integer.class, 
        Long.class, 
        Float.class, 
        Double.class,
        Void.class,
        String.class
        ) );
    //@formatter:on

    @Override
    public boolean allowed( Class<?> type )
    {
        return type != null && ( type.isPrimitive() || BOXED_PRIMITIVES.contains( type ) );
    }

}
