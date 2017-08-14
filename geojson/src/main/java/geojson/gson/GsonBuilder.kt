package geojson.gson

import com.google.gson.GsonBuilder
import geojson.GeoJsonObject
import geojson.Position
import geojson.geometry.Geometry

/**
 * Created by Chris on 30/07/2017.
 */
fun GsonBuilder.registerGeoJsonTypeAdapters() : GsonBuilder {
    this.registerTypeAdapter( GeoJsonObject::class.java, GeoJsonObjectDeserializer() )
    this.registerTypeAdapter( Geometry     ::class.java, GeometryDeserializer()      )
    this.registerTypeAdapter( Position     ::class.java, PositionTypeAdapter()       )

    GeometryKey.values().forEach {
        this.registerTypeAdapter( it.`class`.java, GeometrySerializer() )
    }

    return this
}