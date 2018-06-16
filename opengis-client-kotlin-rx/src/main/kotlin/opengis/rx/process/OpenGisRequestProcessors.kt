package opengis.rx.process

import io.reactivex.Observable
import opengis.model.app.request.OpenGisRequest
import opengis.process.OpenGisRequestProcessor
import opengis.process.Outcome
import kotlin.reflect.KClass

/**
 * Execute an OpenGIS request as a reactive Observable.
 */
fun <Result:Any> OpenGisRequestProcessor.execute(
        request: OpenGisRequest<Result>,
        resultType: KClass<Result>
) : Observable<Result> = Outcome.toObservable { callback ->
    execute(request,resultType,callback)
}
