package opengis.process

import io.reactivex.Observable
import opengis.model.request.OpenGisRequest
import kotlin.reflect.KClass


fun <Result:Any> OpenGisClient.execute(
        request    : OpenGisRequest<Result>,
        resultType : KClass<Result>
    ) : Observable<Result> = Observable.create { emitter ->
    this.execute( request, resultType ) { result: Result ->
        emitter.onNext(result)
        emitter.onComplete()
    }
}

inline fun <reified Result:Any> OpenGisClient.execute( request: OpenGisRequest<Result> ) : Observable<Result> = this.execute(
        request    = request,
        resultType = Result::class
    )