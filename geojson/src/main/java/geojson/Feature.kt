package geojson

import geojson.geometry.Geometry

data class Feature(
        val id         : String,
        val geometry   : Geometry<*>,
        val properties : Map<String,Any>
    ) : GeoJsonObject()