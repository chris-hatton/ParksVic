package geojson.gson

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import geojson.geometry.Geometry

class GeometryTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T>? {
        if( gson == null || type == null || !Geometry::class.java.isAssignableFrom(type.rawType) ) { throw Exception() }

        val delegateAdapter = gson.getDelegateAdapter(this, type)

        @Suppress("UNCHECKED_CAST")
        return GeometryTypeAdapter( delegateAdapter, type ) as TypeAdapter<T>
    }

    private class GeometryTypeAdapter<T>( val delegateAdapter: TypeAdapter<T>, type: TypeToken<T> ) : TypeAdapter<Geometry<*>>() {

        override fun write(out: JsonWriter?, value: Geometry<*>?) {
            if( out == null || value == null ) { return }

            @Suppress("UNCHECKED_CAST")
            val tree : JsonElement = delegateAdapter.toJsonTree(value)
            val key : GeometryKey = GeometryKey.forGeometry(value)
            (tree as JsonObject).addProperty( GeometryKey.key, key )
        }

        override fun read(`in`: JsonReader?): Geometry<*> {
            if( `in` == null ) { throw Exception() }


        }

    }
}

