package org.chrishatton.crosswind.ui.presenter

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.chrishatton.crosswind.ui.contract.ViewContract
import org.chrishatton.crosswind.util.Nullable
import kotlin.properties.Delegates

abstract class Presenter<T: ViewContract> {

    private val viewSubject : Subject<Nullable<T>> = BehaviorSubject.createDefault<Nullable<T>>(Nullable())
    val viewStream : Observable<Nullable<T>> = viewSubject.hide()
    var view : T? by Delegates.observable(initialValue = null) { _,oldView:T?,newView:T? ->
            oldView?.let { onViewDetached(it) }
            newView?.let { view ->
                viewSubject.onNext(Nullable(view))
                onViewAttached( view, viewSubscriptions )
            }
        }

    private val viewSubscriptions      = CompositeDisposable()
    private val activeSubscriptions    = CompositeDisposable()
    private val presenterSubscriptions = CompositeDisposable()

    protected open fun onViewAttached( view: T, viewSubscriptions: CompositeDisposable ) = Unit
    protected open fun onViewDetached( view: T ) = viewSubscriptions.dispose()

    protected val subscriptions = CompositeDisposable()

    fun create()  = onCreate( presenterSubscriptions )
    fun pause()   = onPause()
    fun resume()  = onResume( activeSubscriptions )
    fun destroy() = onDestroy()

    protected open fun onCreate( subscriptions: CompositeDisposable ) = Unit
    protected open fun onResume( activeSubscriptions: CompositeDisposable ) = Unit
    protected open fun onPause()   = activeSubscriptions.dispose()
    protected open fun onDestroy() = presenterSubscriptions.dispose()
}
