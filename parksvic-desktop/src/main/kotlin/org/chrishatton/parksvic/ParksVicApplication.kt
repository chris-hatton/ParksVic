package org.chrishatton.parksvic

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import org.chrishatton.crosswind.Environment
import org.chrishatton.crosswind.environment
import org.chrishatton.parksvic.view.SitesView
import tornadofx.*

class ParksVicApplication : App() {

    override val primaryView = SitesView::class

    init {
        environment = Environment(
                uiScheduler = JavaFxScheduler.platform(),
                logger      = { message -> println(message) }
        )
    }

}