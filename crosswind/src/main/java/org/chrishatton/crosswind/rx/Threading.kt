package org.chrishatton.crosswind.rx

import io.reactivex.Observable
import org.chrishatton.crosswind.environment


fun <T> Observable<T>.observeOnUiThread() : Observable<T> {
    return this.observeOn( environment!!.uiScheduler )
}

fun <T> Observable<T>.subscribeOnUiThread() : Observable<T> {
    return this.subscribeOn( environment!!.uiScheduler )
}

fun <T> Observable<T>.observeOnLogicThread() : Observable<T> {
    return this.observeOn( environment!!.logicScheduler )
}

fun <T> Observable<T>.subscribeOnLogicThread() : Observable<T> {
    return this.subscribeOn( environment!!.logicScheduler )
}

fun assertMainThread() {
    environment!!.isOnUiThread()
}

fun assertNotMainThread() {
    !environment!!.isOnUiThread()
}

fun doOnMainThread( task: ()->Unit ) {
    environment!!.uiScheduler.scheduleDirect(task)
}

fun doOnLogicThread( task: ()->Unit ) {
    environment!!.logicScheduler.scheduleDirect(task)
}
