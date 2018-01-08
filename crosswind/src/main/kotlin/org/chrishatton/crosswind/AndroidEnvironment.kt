package org.chrishatton.crosswind

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

var environment : Environment? = null

data class Environment(
    var uiScheduler    : Scheduler,
    var logicScheduler : Scheduler = Schedulers.single(),
    var logger         : (String)->Unit,
    val isOnUiThread   : ()->Boolean
) {
//    companion object {
//        var current : Environment? = null
//    }
}