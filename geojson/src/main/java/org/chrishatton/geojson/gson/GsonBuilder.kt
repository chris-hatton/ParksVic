package org.chrishatton.geojson.gson

import com.google.gson.GsonBuilder
import org.chrishatton.geojson.GeoJsonObject
import org.chrishatton.geojson.geometry.Geometry

/**
 * Created by Chris on 30/07/2017.
 */
fun GsonBuilder.registerGeoJsonTypeAdapters() {
    this.registerTypeAdapter( GeoJsonObject::class.java, GeoJsonObjectDeserializer() )
    this.registerTypeAdapter( Geometry     ::class.java, GeometryDeserializer()      )
}