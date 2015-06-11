package com.xpbytes.gson.hal;


import com.google.gson.annotations.SerializedName;

@HalResource
public class TestResource extends HalResourceBase {

    public static final String TEST_SERIALIZED_NAME = "serialized_name";

    @SerializedName( TestResource.TEST_SERIALIZED_NAME )
    String serializedField;
    String regularField;
    transient String transientField;

}
