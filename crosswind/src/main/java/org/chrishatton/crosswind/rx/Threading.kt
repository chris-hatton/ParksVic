package org.chrishatton.crosswind.rx

import io.reactivex.Observable
import org.chrishatton.crosswind.environment


fun <T> Observable<T>.observeOnUiThread() : Observable<T> {
    return this.observeOn( environment!!.uiScheduler )
}

fun <T> Observable<T>.subscribeOnUiThread() : Observable<T> {
    return this.subscribeOn( environment!!.uiScheduler )
}
