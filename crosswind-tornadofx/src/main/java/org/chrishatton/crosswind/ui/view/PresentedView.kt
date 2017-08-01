package org.chrishatton.crosswind.ui.view

import org.chrishatton.crosswind.ui.contract.ViewContract
import org.chrishatton.crosswind.ui.presenter.Presenter
import tornadofx.*

abstract class PresentedView<out T: ViewContract>(title:String) : View(title) {

    lateinit var presenter : Presenter<T>

    private var isCreated : Boolean = false

    override fun onDock() {
        super.onDock()

        if(!isCreated) {
            presenter.onCreate()
            isCreated = true
        }

        presenter.onResume()
    }

    override fun onUndock() {
        presenter.onPause()
        super.onUndock()
    }

    private fun finalize() {
        presenter.onDestroy()
    }
}
