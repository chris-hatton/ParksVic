package org.chrishatton.crosswind.ui.view

import android.os.Bundle
import org.chrishatton.crosswind.ui.contract.ViewContract
import org.chrishatton.crosswind.ui.presenter.Presenter

abstract class PresentedFragment<T,P> : android.app.Fragment()
where T: ViewContract, P:Presenter<T> {

    lateinit var presenter : P
        protected set

    protected abstract fun createPresenter( view: T ) : P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        val view = this as? T ?: throw Exception("${this::class} must implement ViewContract T")
        presenter = createPresenter( view )

        presenter.create()
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }

    override fun onPause() {
        presenter.pause()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }
}