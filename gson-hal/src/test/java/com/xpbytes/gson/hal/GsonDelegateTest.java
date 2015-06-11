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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith( JUnit4.class )
public class GsonDelegateTest {

    private static final String REGULAR_FIELD_VALUE = "regular_field_value";
    private static final String SERIALIZED_FIELD_VALUE = "serialized_field_value";
    private static final String TRANSIENT_FIELD_VALUE = "transient_field_value";

    private Gson gson, gsonOriginal;
    private TestResource serializableResource;

    @Before public void setup() {
        gson = new GsonBuilder()
            .registerTypeAdapterFactory( new HalTypeAdapterFactory() )
            .create();

        gsonOriginal = new GsonBuilder()
            .create();

        serializableResource = new TestResource();

        serializableResource.regularField = REGULAR_FIELD_VALUE;
        serializableResource.serializedField = SERIALIZED_FIELD_VALUE;
        serializableResource.transientField = TRANSIENT_FIELD_VALUE;
    }

    @Test
    public void serializationIsStale() {
        String halSerialized = gson.toJson( serializableResource );
        String gsonSerialized = gsonOriginal.toJson( serializableResource );

        assertThat( halSerialized, equalTo( gsonSerialized ) );
    }

}
