package com.xpbytes.gson.hal;


import java.util.List;

public class TestHalResource extends TestResource {

    public static final String TEST_LINK_SERIALIZED_NAME = "link_serialized_name";
    public static final String TEST_EMBED_SERIALIZED_NAME = "embed_serialized_name";
    public static final String TEST_EMBEDS_SERIALIZED_NAME = "embed_serialized_names";

    @HalLink( name = TestHalResource.TEST_LINK_SERIALIZED_NAME )
    String serializedLinkField;

    @HalLink
    String regularLinkField;

    @HalEmbed( name = TestHalResource.TEST_EMBED_SERIALIZED_NAME )
    String serializedEmbedField;

    @HalEmbed
    String embeddedField;

    @HalEmbed( name = TestHalResource.TEST_EMBEDS_SERIALIZED_NAME )
    List<String> embeddedFields;

}
