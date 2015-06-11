package com.xpbytes.gson.hal;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Derk-Jan on 10-6-2015.
 */
public class HalTypeAdapterFactory implements TypeAdapterFactory {

    @Override public <T> TypeAdapter<T> create( Gson gson, TypeToken<T> type ) {

        final TypeAdapter<T> delegate = gson.getDelegateAdapter( this, type );
        final TypeAdapter<JsonElement> basicAdapter = gson.getAdapter( JsonElement.class );
        // If we convert to JSON, isn't the deserializer more appropriate...?

        // Is this a HalResource?
        if ( !HalReflection.isResource( type.getRawType() ) )
            return delegate;

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write( out, value );
            }

            @Override
            public T read( JsonReader in ) throws IOException {
                JsonElement fullJson = basicAdapter.read( in );
                Logger.getGlobal().log( Level.ALL, fullJson.toString() );
                T deserialized = delegate.fromJsonTree( fullJson );

                if ( fullJson.isJsonObject()) {
                    JsonObject fullJsonObject = fullJson.getAsJsonObject();

                    JsonObject linksObject = fullJsonObject.getAsJsonObject( HalConstants.RESERVED_LINKS_ROOT );
                    JsonObject embeddedObject = fullJsonObject.getAsJsonObject( HalConstants.RESERVED_EMBEDDED_ROOT );

                    List<Field> fieldsList = HalReflection.getHalFields( type.getRawType() );
                    for (Field field : fieldsList) {
                        if ( HalReflection.isLink( field ) )
                            readLink( field, linksObject, deserialized );
                        else if ( HalReflection.isEmbed( field ) )
                            readEmbed( field, embeddedObject, deserialized );
                    }

                }

                return deserialized;
            }

            private <A> void readEmbed( Field field, JsonObject rootObject, A deserialized ) {
                HalEmbed embed = field.getAnnotation( HalEmbed.class );
                boolean optional = embed.optional();
                if ( rootObject == null && optional )
                    return;

                String memberName = HalReflection.getJsonFieldName( embed, field );
                JsonParseException missingException = new JsonParseException(
                    String.format( Locale.US,
                        "Expected embed `%s` in the embedded root `%s` to be present",
                        memberName,
                        HalConstants.RESERVED_EMBEDDED_ROOT
                    )
                );

                if ( rootObject == null )
                    throw missingException;

                boolean exists = rootObject.has( memberName );
                if ( !exists ) {
                    if ( optional )
                        return;
                    throw missingException;
                }

                Type innerType = HalReflection.getFieldItemizedType( field );
                //Class outerType = HalReflection.getFieldType( field );
                JsonElement element = rootObject.get( memberName );

                // This gson.fromJson call will actually call into the proper stack to recursively
                // deserialize embeds and set their links where necessary.
                HalReflection.setEmbed( field, gson.fromJson( element, innerType ), deserialized );
            }

            private <A> void readLink( Field field, JsonObject rootObject, A deserialized ) {

                HalLink link = field.getAnnotation( HalLink.class );
                boolean optional = link.optional();
                if ( rootObject == null && optional )
                    return;

                String memberName = HalReflection.getJsonFieldName( link, field );
                JsonParseException missingException = new JsonParseException(
                    String.format( Locale.US,
                        "Expected link `%s` in the links root `%s` to be present",
                        memberName,
                        HalConstants.RESERVED_LINKS_ROOT
                    )
                );

                if ( rootObject == null )
                    throw missingException;

                boolean exists = rootObject.has( memberName );
                if ( !exists ) {
                    if ( optional )
                        return;
                    throw missingException;
                }

                // If this is not a descendant of a HalLinkObject, we better treat it as one.
                Class<?> innerType = HalReflection.getFieldItemizedType( field );
                if ( !innerType.isAssignableFrom( HalLinkObject.class ) )
                    innerType = HalLinkObject.class;

                //Class outerType = HalReflection.getFieldType( field );
                JsonElement element = rootObject.get( memberName );

                // TODO support collections

                /*if ( Collection.class.isAssignableFrom( outerType ) )
                {
                    innerType.
                    //noinspection unchecked
                    Class<Collection> collectionClass = (Class<Collection>)outerType;
                    Collection collection = gson.fromJson( element, collectionClass );
                }

                field.getType()

                if ( element.isJsonArray() ) {
                    for ( JsonElement element1 : element.getAsJsonArray() )
                        HalReflection.setLink( field, , deserialized );
                } else {
                 */
                HalReflection.setLink( field, gson.fromJson( element, (Type)innerType ), deserialized );
            }

        }.nullSafe();
    }
}
