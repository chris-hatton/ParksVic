package opengis.rx.process

import io.reactivex.Observable
import opengis.model.app.OpenGisHttpServer
import opengis.process.Outcome
import opengis.process.ServerListLoader
import java.net.URL

fun ServerListLoader.load(listURL: URL) : Observable<Iterable<OpenGisHttpServer>> {
    return Outcome.toObservable { callback ->
        load( listURL, callback )
    }
}