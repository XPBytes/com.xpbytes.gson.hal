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
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith( JUnit4.class )
public class HalEmbedTest {

    private Gson gson;

    @Before public void setup() {
        gson = new GsonBuilder()
            .registerTypeAdapterFactory( new HalTypeAdapterFactory() )
            .create();
    }

    @Test
    public void deserializeEmbed() {
        String baz = "foobarbaz";
        String link = "/api/deserializeEmbed";
        String simpleEmbed = String.format( Locale.US, "{ 'baz': '%s', '_links': { 'self': { 'href': '%s' } } }", baz, link );
        String gsonSerialized = String.format( Locale.US, "{ '_embedded': { 'simpleEmbed': %s } }", simpleEmbed );

        HalEmbedTestResource deserialized = gson.fromJson( gsonSerialized, HalEmbedTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.simpleEmbed, is( notNullValue() ) );
        assertThat( deserialized.simpleEmbed.baz, is( baz ) );
        assertThat( deserialized.simpleEmbed.self, is( link ) );
    }

    @Test
    public void deserializeMultipleEmbeds() {
        String fooLink = "/api/deserializeMultipleEmbeds/foo";
        String barLink = "/api/deserializeMultipleEmbeds/bar";
        String foobaz = "foobaz";
        String barbaz = "barbaz";
        String fooEmbed = String.format( Locale.US, "{ 'baz': '%s', '_links': { 'self': { 'href': '%s' } } }", foobaz, fooLink );
        String barEmbed = String.format( Locale.US, "{ 'baz': '%s', '_links': { 'self': { 'href': '%s' } } }", barbaz, barLink );
        String gsonSerialized = String.format( Locale.US, "{ '_embedded': { 'foo': %s, 'bar': %s } }", fooEmbed, barEmbed );

        HalEmbedMultipleTestResource deserialized = gson.fromJson( gsonSerialized, HalEmbedMultipleTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.foo, is( notNullValue() ) );
        assertThat( deserialized.foo.baz, is( foobaz ) );
        assertThat( deserialized.foo.self, is( fooLink ) );
        assertThat( deserialized.bar, is( notNullValue() ) );
        assertThat( deserialized.bar.baz, is( barbaz ) );
        assertThat( deserialized.bar.self, is( barLink ) );
    }


    @Test
    public void deserializeNamedEmbed() {
        String namedLink = "/api/deserializeNamedEmbed/named";
        String doublyNamedLink = "/api/deserializeNamedEmbed/doublyNamed";
        String namedBaz = "namedBarBaz";
        String doublyNamedBaz = "doublyNamedBarBaz";
        String namedEmbed = String.format( Locale.US, "{ 'baz': '%s', '_links': { 'self': { 'href': '%s' } } }", namedBaz, namedLink );
        String doublyNamedEmbed = String.format( Locale.US, "{ 'baz': '%s', '_links': { 'self': { 'href': '%s' } } }", doublyNamedBaz, doublyNamedLink );
        String gsonSerialized = String.format( Locale.US, "{ '_embedded': { 'named': %s, 'overridden_name': %s } }", namedEmbed, doublyNamedEmbed );

        HalEmbedNamedTestResource deserialized = gson.fromJson( gsonSerialized, HalEmbedNamedTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.namedEmbed, is( notNullValue() ) );
        assertThat( deserialized.namedEmbed.baz, is( namedBaz ) );
        assertThat( deserialized.namedEmbed.self, is( namedLink ) );
        assertThat( deserialized.overrideEmbed, is( notNullValue() ) );
        assertThat( deserialized.overrideEmbed.baz, is( doublyNamedBaz ) );
        assertThat( deserialized.overrideEmbed.self, is( doublyNamedLink ) );
    }

    @Test
    public void deserializeMissingOptionalEmbed() {
        String requiredBaz = "requiredBarBaz";
        String requiredLink = "/api/deserializeOptionalLink/required";
        String requiredEmbed = String.format( Locale.US, "{ 'baz': '%s', '_links': { 'self': { 'href': '%s' } } }", requiredBaz, requiredLink );
        String gsonSerialized = String.format( Locale.US, "{ '_embedded': { 'requiredEmbed': %s } }", requiredEmbed );

        HalEmbedOptionalTestResource deserialized = gson.fromJson( gsonSerialized, HalEmbedOptionalTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.requiredEmbed, is( notNullValue() ) );
        assertThat( deserialized.requiredEmbed.baz, is( requiredBaz ) );
        assertThat( deserialized.requiredEmbed.self, is( requiredLink ) );
        assertThat( deserialized.optionalEmbed, is( nullValue() ) );
    }

    @Test
    public void deserializePresentOptionalEmbed() {
        String optionalBaz = "optionalFooBarBaz";
        String requiredBaz = "requiredBarBaz";
        String optionalLink = "/api/deserializePresentOptionalEmbed/optional";
        String requiredLink = "/api/deserializePresentOptionalEmbed/required";
        String optionalEmbed = String.format( Locale.US, "{ 'baz': '%s', '_links': { 'self': { 'href': '%s' } } }", optionalBaz, optionalLink );
        String requiredEmbed = String.format( Locale.US, "{ 'baz': '%s', '_links': { 'self': { 'href': '%s' } } }", requiredBaz, requiredLink );

        String gsonSerialized = String.format( Locale.US, "{ '_embedded': { 'requiredEmbed': %s, 'optionalEmbed': %s } }", requiredEmbed, optionalEmbed );

        HalEmbedOptionalTestResource deserialized = gson.fromJson( gsonSerialized, HalEmbedOptionalTestResource.class );
        assertThat( deserialized, is( notNullValue() ) );
        assertThat( deserialized.requiredEmbed, is( notNullValue() ) );
        assertThat( deserialized.requiredEmbed.baz, is( requiredBaz ) );
        assertThat( deserialized.requiredEmbed.self, is( requiredLink ) );
        assertThat( deserialized.optionalEmbed, is( notNullValue() ) );
        assertThat( deserialized.optionalEmbed.baz, is( optionalBaz ) );
        assertThat( deserialized.optionalEmbed.self, is( optionalLink ) );
    }

    @Test( expected = JsonParseException.class )
    public void deserializeMissingRequiredEmbed() {
        String optionalLink = "/api/deserializeMissingRequiredLink";
        String optionalBaz = "optionalFooBarBaz";
        String optionalEmbed = String.format( Locale.US, "{ 'baz': '%s', '_links': { 'self': { 'href': '%s' } } }", optionalBaz, optionalLink );
        String gsonSerialized = String.format( Locale.US, "{ '_embedded': { 'optionalEmbed': { 'href': '%s' } } }", optionalEmbed );
        gson.fromJson( gsonSerialized, HalEmbedOptionalTestResource.class );
    }

    @Test( expected = JsonParseException.class )
    public void deserializeMissingAllEmbed() {
        String gsonSerialized = "{}";
        gson.fromJson( gsonSerialized, HalEmbedOptionalTestResource.class );
    }

    @HalResource class SimpleEmbed { public String baz; @HalLink public String self; }
    @HalResource class HalEmbedTestResource { @HalEmbed public SimpleEmbed simpleEmbed; }
    @HalResource class HalEmbedMultipleTestResource {
        @HalEmbed public SimpleEmbed foo;
        @HalEmbed public SimpleEmbed bar;
    }
    @HalResource class HalEmbedNamedTestResource {
        @HalEmbed( name = "named" ) public SimpleEmbed namedEmbed;
        @SerializedName( "overridden_name" ) @HalEmbed( name = "original_name" )
        public SimpleEmbed overrideEmbed;

    }
    @HalResource class HalEmbedOptionalTestResource {
        @HalEmbed( optional = false ) public SimpleEmbed requiredEmbed;
        @HalEmbed( optional = true ) public SimpleEmbed optionalEmbed;
    }

}
