package opengis.process

import io.reactivex.Observable
import opengis.model.app.request.OpenGisRequest
import kotlin.reflect.KClass


fun <Result:Any> OpenGisClient.execute(
        request    : OpenGisRequest<Result>,
        resultType : KClass<Result>
    ) : Observable<Result> = Observable.create { emitter ->

    val callback = object : OpenGisClient.Callback<Result> {
        override fun success(result: Result) {
            emitter.onNext(result)
            emitter.onComplete()
        }
        override fun error(error: Throwable) {
            emitter.onError(error)
        }
    }

    this.execute( request, resultType, callback )
}

inline fun <reified Result:Any> OpenGisClient.execute( request: OpenGisRequest<Result> ) : Observable<Result> = this.execute(
        request    = request,
        resultType = Result::class
    )