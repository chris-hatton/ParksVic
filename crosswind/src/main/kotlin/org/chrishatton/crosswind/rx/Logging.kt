package org.chrishatton.crosswind.rx

import io.reactivex.Observable
import org.chrishatton.crosswind.Crosswind

fun <T> Observable<T>.logOnNext( message: (T)->String ) : Observable<T> = this.doOnNext {
    Crosswind.environment.logger(message(it))
}