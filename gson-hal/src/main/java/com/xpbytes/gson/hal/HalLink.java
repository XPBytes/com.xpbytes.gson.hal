package com.xpbytes.gson.hal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An HAL-link is a {@link HalLinkObject} or it's simplified Uri. The JSON equivalent
 *
 * @see com.google.gson.annotations.SerializedName
 * @see com.google.gson.FieldNamingPolicy
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface HalLink  {
    /**
     * The name of the link such as "self" or "ea:something"
     * @return the desired name of the field when it is serialized
     */
    String name() default "";


    boolean optional() default false;
}
