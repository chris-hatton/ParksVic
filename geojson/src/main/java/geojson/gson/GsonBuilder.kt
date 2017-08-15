package geojson.gson

import com.google.gson.GsonBuilder
import geojson.GeoJsonObject
import geojson.Position
import geojson.geometry.Geometry

/**
 * Created by Chris on 30/07/2017.
 */
fun GsonBuilder.registerGeoJsonTypeAdapters() : GsonBuilder {

    this.registerTypeAdapter( Position     ::class.java, PositionTypeAdapter()       )

    this.registerTypeAdapterFactory( GeoJsonObjectTypeAdapterFactory() )

    return this
}