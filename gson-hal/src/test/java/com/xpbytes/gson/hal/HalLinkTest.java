package com.xpbytes.gson.hal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.URI;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith( JUnit4.class )
public class HalLinkTest {

    private Gson gson;

    @Before public void setup() {
        gson = new GsonBuilder()
            .registerTypeAdapterFactory( new HalTypeAdapterFactory() )
            .create();
    }

    @Test
    public void deserializeStringLink() {
        String link = "/api/deserializeStringLink";
        String gsonSerialized = String.format( Locale.US, "{ '_links': { 'stringLink': { 'href' : '%s' } } }", link );

        HalLinkStringTestResource deserialized = gson.fromJson( gsonSerialized, HalLinkStringTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.stringLink, equalTo( link ) );
    }

    @Test
    public void deserializeMultipleLinks() {
        String foo = "/api/foo";
        String bar = "/api/bar";
        String gsonSerialized = String.format( Locale.US, "{ '_links': { 'foo': { 'href' : '%s' }, 'bar': { 'href' : '%s' } } }", foo, bar );

        HalLinkMultipleTestResource deserialized = gson.fromJson( gsonSerialized, HalLinkMultipleTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.foo, equalTo( foo ) );
        assertThat( deserialized.bar, equalTo( bar ) );
    }

    @Test
    public void deserializeUriLink() {
        String link = "/api/deserializeUriLink";
        String gsonSerialized = String.format( Locale.US, "{ '_links': { 'uriLink': { 'href' : '%s' } } } ", link );

        HalLinkUriTestResource deserialized = gson.fromJson( gsonSerialized, HalLinkUriTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.uriLink, equalTo( URI.create( link ) ) );
    }

    @Test
    public void deserializeLinkObjectLink() {
        String link = "/api/deserializeLinkObjectLink";
        String gsonSerialized = String.format( Locale.US, "{ '_links': { 'linkObjectLink': { 'href' : '%s' } } }", link );

        HalLinkLinkObjectTestResource deserialized = gson.fromJson( gsonSerialized, HalLinkLinkObjectTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.linkObjectLink.getHref(), equalTo( link ) );
    }

    @Test
    public void deserializeNamedLink() {
        String foo = "/api/foo";
        String bar = "/api/bar";
        String gsonSerialized = String.format( Locale.US, "{ '_links': { 'named': { 'href' : '%s' }, 'overridden_name': { 'href' : '%s' } } }", foo, bar );

        HalLinkNamedTestResource deserialized = gson.fromJson( gsonSerialized, HalLinkNamedTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.namedLink, equalTo( foo ) );
        assertThat( deserialized.overrideLink, equalTo( bar ) );
    }

    @Test
    public void deserializeMissingOptionalLink() {
        String link = "/api/deserializeOptionalLink";
        String gsonSerialized = String.format( Locale.US, "{ '_links': { 'requiredLink': { 'href' : '%s' } } }", link );

        HalLinkOptionalTestResource deserialized = gson.fromJson( gsonSerialized, HalLinkOptionalTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.requiredLink, equalTo( link ) );
        assertThat( deserialized.optionalLink, is( nullValue() ) );
    }

    @Test
    public void deserializePresentOptionalLink() {
        String foo = "/api/foo";
        String bar = "/api/bar";
        String gsonSerialized = String.format( Locale.US, "{ '_links': { 'requiredLink': { 'href' : '%s' }, 'optionalLink': { 'href' : '%s' } } }", foo, bar );

        HalLinkOptionalTestResource deserialized = gson.fromJson( gsonSerialized, HalLinkOptionalTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.requiredLink, equalTo( foo ) );
        assertThat( deserialized.optionalLink, equalTo( bar ) );
    }

    @Test( expected = JsonParseException.class )
    public void deserializeMissingRequiredLink() {
        String foo = "/api/deserializeMissingRequiredLink";
        String gsonSerialized = String.format( Locale.US, "{ '_links': { 'optionalLink': { 'href' : '%s' } } }", foo );
        gson.fromJson( gsonSerialized, HalLinkOptionalTestResource.class );
    }

    @Test( expected = JsonParseException.class )
    public void deserializeMissingAllLink() {
        String gsonSerialized = "{}";
        gson.fromJson( gsonSerialized, HalLinkOptionalTestResource.class );
    }

    @HalResource class HalLinkStringTestResource { @HalLink public String stringLink; }
    @HalResource class HalLinkUriTestResource { @HalLink public URI uriLink; }
    @HalResource class HalLinkLinkObjectTestResource { @HalLink public HalLinkObject linkObjectLink; }
    @HalResource class HalLinkMultipleTestResource {
        @HalLink public String foo;
        @HalLink public String bar;
    }
    @HalResource class HalLinkNamedTestResource {
        @HalLink( name = "named" ) public String namedLink;
        @SerializedName( "overridden_name" ) @HalLink( name = "named" ) public String overrideLink;

    }
    @HalResource class HalLinkOptionalTestResource {
        @HalLink( optional = false ) public String requiredLink;
        @HalLink( optional = true ) public String optionalLink;
    }

}
