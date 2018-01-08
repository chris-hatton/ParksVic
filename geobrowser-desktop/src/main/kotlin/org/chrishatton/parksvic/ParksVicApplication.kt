package org.chrishatton.geobrowser

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import javafx.application.Platform
import org.chrishatton.crosswind.Environment
import org.chrishatton.crosswind.environment
import org.chrishatton.geobrowser.view.SitesView
import tornadofx.*

class ParksVicApplication : App() {

    override val primaryView = SitesView::class

    init {
        environment = Environment(
            uiScheduler  = JavaFxScheduler.platform(),
            logger       = { message -> println(message) },
            isOnUiThread = { Platform.isFxApplicationThread() }
        )
    }
}