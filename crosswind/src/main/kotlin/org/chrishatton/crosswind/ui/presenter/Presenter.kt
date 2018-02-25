package org.chrishatton.crosswind.ui.presenter

import io.reactivex.disposables.CompositeDisposable
import org.chrishatton.crosswind.ui.contract.ViewContract

public abstract class Presenter<out T: ViewContract> {

    abstract val view : T

    protected val subscriptions = CompositeDisposable()

    abstract fun onCreate()
    abstract fun onResume()
    abstract fun onPause()
    abstract fun onDestroy()
}
