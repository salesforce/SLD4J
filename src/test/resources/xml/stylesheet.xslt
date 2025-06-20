<?xml version="1.0"?>
<stylesheet xmlns="http://www.w3.org/1999/XSL/Transform"  xmlns:foo="urn:foo" version="1.0">
    <output method="xml"/>

    <param name="foo"/>

    <template match="@*|node()">
        <copy>
            <apply-templates select="@*|node()"/>
        </copy>
    </template>

    <template match="foo:my/foo:hello">
        <copy>
            <foo:world>
                foo is : <value-of select="$foo"/>
            </foo:world>
        </copy>

    </template>
</stylesheet>