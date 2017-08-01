package org.chrishatton.crosswind.ui.view

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import org.chrishatton.crosswind.ui.contract.ViewContract
import org.chrishatton.crosswind.ui.presenter.Presenter

abstract class PresentedActivity<T,P> : FragmentActivity()
    where T: ViewContract, P:Presenter<T> {

    protected lateinit var presenter : P

    abstract fun createPresenter() : P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = createPresenter()

        presenter.onCreate( view = this )
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}