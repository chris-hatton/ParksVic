package org.chrishatton.crosswind.ui.presenter

import io.reactivex.disposables.CompositeDisposable
import org.chrishatton.crosswind.ui.contract.ViewContract

abstract class Presenter<T: ViewContract> {

    protected abstract val view : T

    protected val subscriptions = CompositeDisposable()

    abstract fun onCreate( view: T )
    abstract fun onResume()
    abstract fun onPause()
    abstract fun onDestroy()
}
