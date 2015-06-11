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

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith( JUnit4.class )
public class SampleTest {

    private Gson gson;
    private String sampleJson;

    @Before public void setup() {
        gson = new GsonBuilder()
            .registerTypeAdapterFactory( new HalTypeAdapterFactory() )
            .create();

        this.sampleJson =

    "{\"id\":\"19f4f735-8a2d-43e4-b70d-a8f9a26fec0c\",\"name\":\"Sample Name\",\"_links\":{\"self" +
    "\":{\"href\":\"/api/samples/19f4f735-8a2d-43e4-b70d-a8f9a26fec0c\"},\"next\":{\"href\":\"/ap" +
    "i/samples/next/{id}\",\"templated\":true},\"last\":{\"href\":\"/api/samples/last\"},\"foo:ba" +
    "r\":{\"href\":\"/foo/bar/sample\"}},\"_embedded\":{\"resource\":{\"foo\":324,\"_links\":{\"s" +
    "elf\":{\"href\":\"/api/embedded/324\"}}}}}";

    }

    @Test
    public void samplerSuccess() {
        SampleResource resource = gson.fromJson( sampleJson, SampleResource.class );
        assertThat( resource, is( notNullValue() ) );
    }

    @Test
    public void samplerFields() {
        SampleResource resource = gson.fromJson( sampleJson, SampleResource.class );

        // fields
        assertThat( resource.id, is( equalTo( "19f4f735-8a2d-43e4-b70d-a8f9a26fec0c" ) ) );
        assertThat( resource.name, is( equalTo( "Sample Name" ) ) );
    }

    @Test
    public void halSpecific() {
        SampleResource resource = gson.fromJson( sampleJson, SampleResource.class );

        // hal-specific
        assertThat( resource.self, is( notNullValue() ) );
        assertThat( resource.next, is( notNullValue() ) );
        assertThat( resource.optionalLink, is( nullValue() ) );
        assertThat( resource.namedLink, is( notNullValue() ) );
        assertThat( resource.resource, is( notNullValue() ) );
        assertThat( resource.optionalResource, is( nullValue() ) );
    }

    @Test
    public void halLinks() {
        SampleResource resource = gson.fromJson( sampleJson, SampleResource.class );

        // links
        assertThat( resource.self, is( URI.create( "/api/samples/19f4f735-8a2d-43e4-b70d-a8f9a26fec0c" ) ) );
        assertThat( resource.next.getHref(), is( equalTo( "/api/samples/next/{id}" ) ) );
        assertThat( resource.next.templated, is( true ) );
        assertThat( resource.last, is( equalTo( "/api/samples/last" ) ) );
    }

    @Test
    public void halEmbeds() {
        SampleResource resource = gson.fromJson( sampleJson, SampleResource.class );

        // embedded
        assertThat( resource.resource.foo, is( 324 ) );
        assertThat( resource.resource.self, is( equalTo( "/api/embedded/324" ) ) );
    }

    @HalResource
    public class SampleResource {

        String id;
        String name;

        @HalLink URI self;
        @HalLink HalLinkObject next;
        @HalLink String last;

        @HalLink( optional = true )
        String optionalLink;

        @HalLink( name = "foo:bar" )
        String namedLink;

        @HalEmbed
        MyEmbeddedResource resource;

        @HalEmbed( optional = true )
        MyEmbeddedResource optionalResource;
    }

    @HalResource
    public class MyEmbeddedResource {
        int foo;
        @HalLink String self;
    }

}
