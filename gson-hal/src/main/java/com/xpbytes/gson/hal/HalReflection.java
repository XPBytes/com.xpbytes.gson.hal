package com.xpbytes.gson.hal;

import com.google.gson.FieldAttributes;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class HalReflection {

    static Map<Class<?>, List<Field>> fieldCache = new HashMap<>(  );
    static Map<Class<?>, List<Field>> halFieldCache = new HashMap<>(  );


    /**
     * Gets all fields of a type
     *
     * @see #getAllFields(Class)
     *
     * @param type the type
     * @return a list of fields
     */
    static synchronized List<Field> getAllFields( Class<?> type ) {
        return getAllFields( new ArrayList<>(), type );
    }

    /**
     * Gets the fields of a type
     *
     * @param fields the destination list
     * @param type the type
     * @return all the fields for this type and its subtypes
     */
    static synchronized List<Field> getAllFields( List<Field> fields, Class<?> type ) {
        // TODO save entire chain? Bottom up save cache?. Test to see speed gain
        List<Field> fieldsForOnlyThisClass = fieldCache.getOrDefault( type,
            Arrays.asList( type.getDeclaredFields() )
        );
        fieldCache.put( type, fieldsForOnlyThisClass );

        fields.addAll( fieldsForOnlyThisClass );
        return type.getSuperclass() == null ? fields : getAllFields( fields, type.getSuperclass() );
    }

    /**
     * Gets all HAL fields of a type.
     * The resulting fields are cached.
     *
     * @see #getAllFields(Class)
     *
     * @param type the type
     * @return the list of fields
     */
    static synchronized List<Field> getHalFields( Class<?> type ) {
        if ( halFieldCache.containsKey( type ) )
            return halFieldCache.get( type );

        List<Field> result = new ArrayList<>();
        List<Field> fields = getAllFields( type );
        for( Field field : fields ) {
            if ( isLink( field ) || isEmbed( field ) )
                result.add( field );
        }

        halFieldCache.put( type, result );
        return result;
    }

    /**
     * Does the class act like an {@link HalResource}, that is, should it's annotations be
     * processed?
     *
     * @param type the class type
     * @return true if it is
     */
    static boolean isResource( Class type ) {
        return type.isAnnotationPresent( HalResource.class );
    }

    /**
     * Does field represent an embed. Is it annotated with {@link HalLink} or is its type assignable
     * from {@link HalResource}?
     *
     * @param field the field
     * @return true if it does
     */
    static boolean isEmbed( Field field ) {
        return field.isAnnotationPresent( HalEmbed.class ) ||
            isResource( getFieldItemizedType( field ) );

    }

    /**
     * Does field represent a link. Is it annotated with {@link HalLink} or is its type assignable
     * from {@link HalLinkObject}?
     *
     * @param field the field
     * @return true if it does
     */
    static boolean isLink( Field field ) {
        return field.isAnnotationPresent( HalLink.class ) ||
            HalLinkObject.class.isAssignableFrom( getFieldItemizedType( field ) );
    }

    /**
     * Get the JSON field name. Will prefer the {@link SerializedName} annotation over anything.
     * Next is an the {@link HalLink#name()} and {@link HalEmbed#name()} annotation fields and
     * finally the field name itself.
     *
     * @param annotated the annotated value {@link HalLink#name()} or {@link HalEmbed#name()}
     * @param field     the field
     * @return          the field name in the JSON
     */
    private static String getJsonFieldName( String annotated, Field field ) {
        SerializedName annotation = field.getAnnotation( SerializedName.class );
        return annotation == null ?
            stringIsNullOrEmpty( annotated ) ? field.getName() : annotated :
            annotation.value();
    }

    /**
     * Get a JSON field name
     *
     * @see #getJsonFieldName(String, Field)
     *
     * @param link an link
     * @param field a field
     * @return the proper name
     */
    static String getJsonFieldName( HalLink link, Field field ) {
        return getJsonFieldName( link.name(), field );
    }

    /**
     * Get a JSON field name
     *
     * @see #getJsonFieldName(String, Field)
     *
     * @param embed an embed
     * @param field a field
     * @return the proper name
     */
    static String getJsonFieldName( HalEmbed embed, Field field ) {
        return getJsonFieldName( embed.name(), field );

    }

    /**
     * Is a string null or empty?
     * @param s the string to test
     * @return true if it is
     */
    private static boolean stringIsNullOrEmpty( String s ) {
        return s == null || s.length() == 0;
    }

    /**
     * Set a link on a object
     *
     * @param field     the field
     * @param resource  the link object
     * @param object    the object
     * @param <A>       the resulting type of the object
     *
     * @throws ClassCastException if field type is not compatible with {@link HalLinkObject}
     */
    public static <A> void setLink( Field field, HalLinkObject resource, A object ) {
        Class<?> destinationType = getFieldItemizedType( field );
        if ( CharSequence.class.isAssignableFrom( destinationType ) )
            setFieldSafe( field, object, resource.getHref().toString() );
        else if ( URI.class.isAssignableFrom( destinationType ) )
            setFieldSafe( field, object, resource.getHref() );
        else if ( HalLinkObject.class.isAssignableFrom( destinationType ) )
            setFieldSafe( field, object, resource );
        else
            throw new ClassCastException( String.format( Locale.US,
                "Field %s is of type %s and can't be assigned. Should be HalLinkObject, " +
                    "CharSequence or URI",
                field.getName(),
                destinationType.getName() )
            );
    }

    /**
     * Set a embed on a object
     *
     * @param field     the field
     * @param resource  the link object
     * @param object    the object
     * @param <A>       the resulting type of the object
     *
     * @throws ClassCastException if field type is not compatible with {@link HalLinkObject}
     */
    public static <A> void setEmbed( Field field, Object resource, A object ) {
        Class<?> destinationType = getFieldItemizedType( field );
        if ( destinationType.isAssignableFrom( resource.getClass() ) )
            setFieldSafe( field, object, resource );
        else
            throw new ClassCastException( String.format( Locale.US,
                "Field %s is of type %s and can't be assigned.",
                field.getName(),
                destinationType.getName() )
            );
    }

    /**
     * Sets a field on an object with a value and swallows any error
     *
     * @param field     field
     * @param object    object
     * @param value     value
     */
    private static void setFieldSafe( Field field, Object object, Object value ) {
        field.setAccessible( true );
        try {
            field.set( object, value );
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Get the type of the field as an item. Will walk collections to find the inner type.
     *
     * @see #getFieldType(Field)
     * @see #getFieldType(FieldAttributes)
     * @see #getFieldItemizedType(FieldAttributes)
     *
     * @param field the field
     * @return the type
     */
    static Class<?> getFieldItemizedType( Field field ) {
        return getFieldItemizedType( new FieldAttributes( field ) );
    }

    /**
     * Get the type of the field as an item. Will walk collections to find the inner type.
     *
     * @see #getFieldType(Field)
     * @see #getFieldType(FieldAttributes)
     * @see #getFieldItemizedType(Field)
     *
     * @param attributes the field attributes
     * @return the type
     */
    static Class<?> getFieldItemizedType( FieldAttributes attributes ) {
        if ( Collection.class.isAssignableFrom( attributes.getDeclaredClass() ) ) {
            ParameterizedType gType = (ParameterizedType) attributes.getDeclaredType();

            Type[] actualTypeArguments = gType.getActualTypeArguments();
            if ( actualTypeArguments != null && actualTypeArguments.length > 0 )
                return (Class)actualTypeArguments[0];

            return getFieldType( attributes );
        }
        return getFieldType( attributes );
    }

    /**
     * Gets the type of a field.
     *
     * @see #getFieldType(FieldAttributes)
     * @see #getFieldItemizedType(Field)
     * @see #getFieldItemizedType(FieldAttributes)
     *
     * @param field the field
     * @return the type
     */
    static Class<?> getFieldType( Field field ) {
        return getFieldType( new FieldAttributes( field ) );
    }

    /**
     * Gets the type of a field.
     *
     * @see #getFieldType(Field)
     * @see #getFieldItemizedType(Field)
     * @see #getFieldItemizedType(FieldAttributes)
     *
     * @param attributes the field attributes
     * @return the type
     */
    static Class<?> getFieldType( FieldAttributes attributes ) {
        return attributes.getDeclaredClass();
    }
}
