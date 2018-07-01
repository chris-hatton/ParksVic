package org.chrishatton.crosswind.rx

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.chrishatton.crosswind.Crosswind

fun <T> Observable<T>.observeOnUiThread() : Observable<T> {
    return this.observeOn( Crosswind.environment.uiScheduler )
}

fun <T> Observable<T>.subscribeOnUiThread() : Observable<T> {
    return this.subscribeOn( Crosswind.environment.uiScheduler )
}

fun <T> Observable<T>.observeOnLogicThread() : Observable<T> {
    return this.observeOn( Crosswind.environment.logicScheduler )
}

fun <T> Observable<T>.subscribeOnLogicThread() : Observable<T> {
    return this.subscribeOn( Crosswind.environment.logicScheduler )
}

fun <T> Observable<T>.observeOnNetworkThread() : Observable<T> {
    return this.observeOn( Schedulers.io() )
}

fun <T> Observable<T>.subscribeOnNetworkThread() : Observable<T> {
    return this.subscribeOn( Schedulers.io() )
}

fun assertMainThread() = Crosswind.environment.isOnUiThread()

fun assertNotMainThread() {
    !Crosswind.environment.isOnUiThread()
}

fun doOnMainThread( task: ()->Unit ) {
    Crosswind.environment.uiScheduler.scheduleDirect(task)
}

fun doOnLogicThread( task: ()->Unit ) {
    Crosswind.environment.logicScheduler.scheduleDirect(task)
}
