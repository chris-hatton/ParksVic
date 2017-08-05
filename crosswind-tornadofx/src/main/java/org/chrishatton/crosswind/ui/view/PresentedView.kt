package org.chrishatton.crosswind.ui.view

import org.chrishatton.crosswind.ui.contract.ViewContract
import org.chrishatton.crosswind.ui.presenter.Presenter
import tornadofx.*

abstract class PresentedView<T: ViewContract, P:Presenter<T>>(title:String) : View(title) {

    lateinit var presenter : P

    protected abstract fun createPresenter() : P

    private var isCreated : Boolean = false

    override fun onDock() {
        super.onDock()

        if(!isCreated) {
            presenter = createPresenter()
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
