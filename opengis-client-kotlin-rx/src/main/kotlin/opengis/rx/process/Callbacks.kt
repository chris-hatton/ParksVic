package opengis.rx.process

import io.reactivex.Observable
import opengis.process.Callback
import opengis.process.Outcome

fun <T> Outcome.Companion.toObservable( from: (Callback<T>)->Unit ) = Observable.create<T> { emitter ->
    val callback = { outcome:Outcome<T> ->
        with(emitter) {
            when(outcome) {
                is Outcome.Success -> {
                    onNext(outcome.result)
                    onComplete()
                }
                is Outcome.Error -> {
                    onError(outcome.error)
                }
            }
        }
    }
    from(callback)
}!!