/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.parser.xml.stream;

/**
 * Thrown when an XML attack is prevented. Provides information about the exception causes to any catchers.
 * 
 * @author csmith
 */
public class XMLResolutionException
    extends SecurityException
{

    private static final long serialVersionUID = -6447807007766225172L;

    private String errorMessage;

    private Class<?> throwClass;

    private AttackType type;

    /**
     * @return an error message tied to this exception
     */
    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    /**
     * @return the classname of the thrower
     */
    public String getThrowClass()
    {
        return this.throwClass.getSimpleName();
    }

    /**
     * @return the type of failure encountered
     */
    public AttackType getAttackType()
    {
        return this.type;
    }

    /**
     * build a new exception for an attack
     * 
     * @param throwClass the thrower of this exception
     * @param type the attack that was caught
     * @param uri what uri, if any was encountered that caused this issue
     */
    public XMLResolutionException( Class<?> throwClass, AttackType type, String uri )
    {
        this.errorMessage = "Entity resolution using " + type.name() + ": " + uri;
        this.throwClass = throwClass;
        this.type = type;
    }

}
