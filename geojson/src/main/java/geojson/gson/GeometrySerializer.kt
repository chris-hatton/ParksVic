package geojson.gson

import com.google.gson.*
import geojson.geometry.Geometry
import java.lang.reflect.Type
import com.google.gson.TypeAdapter
import com.google.gson.JsonObject
import com.google.gson.JsonElement
import sun.util.locale.provider.LocaleProviderAdapter.getAdapter
import com.google.gson.internal.`$Gson$Types`.getRawType
import com.google.gson.Gson
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Field


/**
 * Created by Chris on 14/08/2017.
 */
class GeometrySerializer : JsonSerializer<Geometry<*>> {

    override fun serialize(src: Geometry<*>?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if( context == null ) { throw Exception() }
        if( src     == null ) { throw Exception() }

        val fields = context.serialize(src,typeOfSrc) as? JsonObject ?: throw Exception()



        val geometryKey : GeometryKey = GeometryKey.values().find { it.`class` == src::class } ?: throw Exception()

        fields.addProperty( "type", geometryKey.name )

        return fields
    }
}

internal class MyClass { //class which we would like to serialiaze/deserialize
    var fields: List<Field>? = null //field is an hierarchy of classes
}

}