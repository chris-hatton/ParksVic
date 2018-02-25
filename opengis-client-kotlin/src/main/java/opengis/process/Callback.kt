package opengis.process

import opengis.model.app.MapViewLayer

typealias Callback<Result> = (Outcome<Result>)->Unit

sealed class Outcome<Result> {
    data class Success<Result>(val result: Result) : Outcome<Result>()
    data class Error<Result>(val error: Throwable) : Outcome<Result>()

    companion object {

        sealed class Exception : kotlin.Exception() {
            data class CombinedOutcomeHasError( val firstError: Throwable ) : Exception()
        }

        /**
         * Function which accumulates the result of several callbacks
         */
        fun <I,R> fold(
                inputs: Collection<I>,
                caller: (I,Callback<R>)->Unit,
                initial: R,
                reduce: (R,R)->R,
                callback: Callback<R>
        ) {
            var remainingResults = inputs.size
            inputs.forEach {
                var result : R = initial
                caller(it) { outcome ->
                    if(remainingResults==0) {
                        return@caller
                    }
                    --remainingResults
                    when(outcome) {
                        is Outcome.Error -> {
                            remainingResults = 0
                            callback( Outcome.Error( error = outcome.error ) )
                            return@caller
                        }
                        is Outcome.Success -> {
                            result = reduce(result,outcome.result)
                            if(remainingResults==0) {
                                callback( Outcome.Success( result ) )
                            }
                        }
                    }
                }
            }
        }
    }
}

