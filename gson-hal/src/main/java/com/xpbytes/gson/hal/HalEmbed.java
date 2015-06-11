package com.xpbytes.gson.hal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface HalEmbed {
    /**
     * The name of the embedded resource such as "myembed" or list of resources such as "myembeds"
     * @return the desired name of the field when it is serialized
     */
    String name() default "";
    boolean optional() default false;
}
