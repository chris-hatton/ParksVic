package org.chrishatton.crosswind.ui.view

import android.app.Activity
import android.os.Bundle
import org.chrishatton.crosswind.rx.assertMainThread
import org.chrishatton.crosswind.rx.doOnMainThread
import org.chrishatton.crosswind.ui.contract.ViewContract
import org.chrishatton.crosswind.ui.presenter.Presenter

abstract class PresentedActivity<T,P> : Activity()
    where T: ViewContract, P:Presenter<T> {

    lateinit var presenter : P
        protected set

    protected abstract fun createPresenter() : P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertMainThread()

        @Suppress("UNCHECKED_CAST")
        doOnMainThread {
            presenter = createPresenter()
            presenter.create()
        }
    }

    override fun onResume() {
        super.onResume()

        assertMainThread()

        doOnMainThread {
            presenter.resume()
        }
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