/**
 * Copyright 2015 Derk-Jan Karrenbeld
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
