# com.xpbytes.gson.hal
**Note:** under development

The Gson-[HAL](http://stateless.co/hal_specification.html) library was created to consume the [overheid.io](https://overheid.io) API's using [Google's Gson](https://github.com/google/gson). In its current iteration it can only deseralize HAL-formatted Json; Serialization is on the 1.0 Roadmap.

````Java
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
    MyEmbeddedResouce resource;
    
    @HalEmbed( optional = true )
    MyEmbeddedResouce optionalResource;
}
  
@HalResource  
public class MyEmbeddedResouce {
    int foo;
    @HalLink self;
}
````
    
````Json
{
    "id":"19f4f735-8a2d-43e4-b70d-a8f9a26fec0c",
    "name":"Sample Name",
    "_links":{
        "self":{
            "href":"/api/samples/19f4f735-8a2d-43e4-b70d-a8f9a26fec0c"
        },
        "next":{
            "href":"/api/samples/next/{id}",
            "templated":true
        },
        "last":{
            "href":"/api/samples/last"
        },
        "foo:bar":{
            "href":"/foo/bar/sample"
        }
    },
    "_embedded":{
        "resource":{
            "foo":324,
            "_links":{
                "self":{
                    "href":"/api/embedded/324"
                }
            }
        }
    }
}
````
## Setup
To enable HAL-deserialization, you need to register the type adapter when creating your Gson object.

````Java
Gson gson = new GsonBuilder()
    .registerTypeAdapterFactory( new HalTypeAdapterFactory() )
    .create();
````

## Missing
Collection deserialization
Serialization
Retrofit adapter for links (? -> return type inferred?)
Android extension to make it all parcelable (?)

## License
    Copyright 2015 - Derk-Jan Karrenbeld
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
            
    
    
