package opengis.process

import com.sun.net.httpserver.Authenticator
import io.reactivex.Observable
import opengis.model.app.request.OpenGisRequest
import kotlin.reflect.KClass

/**
 * Presents the asynchronous result returned by an OpenGisClient callback, as an Rx Observable.
 */
fun <Result:Any> OpenGisRequestProcessor.execute(
        request    : OpenGisRequest<Result>,
        resultType : KClass<Result>
    ) : Observable<Result> = Observable.create { emitter ->

    val callback : Callback<Result> = { outcome ->
        when(outcome) {
            is Outcome.Success -> {
                emitter.onNext(outcome.result)
                emitter.onComplete()
            }
            is Outcome.Error -> {
                emitter.onError(outcome.error)
            }
        }
    }

    this.execute( request, resultType, callback )
}

/**
 * Convenience function allowing the 'expected type' information needed by OpenGisClient, to be
 * inferred by the compiler at point of use.
 */
inline fun <reified Result:Any> OpenGisRequestProcessor.execute(request: OpenGisRequest<Result> ) : Observable<Result> = this.execute(
        request    = request,
        resultType = Result::class
    )