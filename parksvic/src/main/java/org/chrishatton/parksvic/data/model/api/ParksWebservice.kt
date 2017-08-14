package org.chrishatton.parksvic.data.model.api

import io.reactivex.Observable
import geojson.BoundingBox
import geojson.FeatureCollection
import retrofit2.http.GET
import retrofit2.http.Query


interface ParksWebservice {
    @GET("wfs/?service=wfs&request=GetFeature&version=2.0.0&outputFormat=application/json&typeNames=ParksVic:recweb_site")
    fun getParks(@Query("BBOX") boundingBox: BoundingBox? = null) : Observable<FeatureCollection>
}