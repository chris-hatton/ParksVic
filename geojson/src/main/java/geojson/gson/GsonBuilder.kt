package geojson.gson

import com.google.gson.GsonBuilder

/**
 * Created by Chris on 30/07/2017.
 */
fun GsonBuilder.registerGeoJsonTypeAdapters() : GsonBuilder {
    this.registerTypeAdapterFactory( GeoJsonObjectTypeAdapterFactory() )
    return this
}