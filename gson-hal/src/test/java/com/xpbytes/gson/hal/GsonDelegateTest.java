package com.xpbytes.gson.hal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Derk-Jan on 10-6-2015.
 */
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
