package org.chrishatton.crosswind

import io.reactivex.Scheduler

var environment : Environment? = null

data class Environment(
    var uiScheduler : Scheduler,
    var logger      : (String)->Unit
)