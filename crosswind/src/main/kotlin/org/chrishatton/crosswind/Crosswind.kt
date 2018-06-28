package org.chrishatton.crosswind

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates

object Crosswind {

    fun initialize( environment: Environment ) {
        this.environment = environment
    }

    var environment : Environment by Delegates.notNull()
        private set

    data class Environment(
            var uiScheduler    : Scheduler,
            var logicScheduler : Scheduler = Schedulers.single(),
            var logger         : (String)->Unit,
            val isOnUiThread   : ()->Boolean
    )
}
