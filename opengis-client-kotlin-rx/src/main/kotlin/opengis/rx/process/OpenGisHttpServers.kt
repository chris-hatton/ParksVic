package opengis.rx.process

import io.reactivex.Observable
import opengis.model.app.MapViewLayer
import opengis.model.app.OpenGisHttpServer
import opengis.model.app.getMapViewLayers
import opengis.process.OpenGisRequestProcessor
import opengis.process.Outcome

fun OpenGisHttpServer.getMapViewLayers(
        clientProvider : (OpenGisHttpServer)-> OpenGisRequestProcessor
) : Observable<Set<MapViewLayer>> = Outcome.toObservable { callback ->
    this.getMapViewLayers(clientProvider,callback)
}