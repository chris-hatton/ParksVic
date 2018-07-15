package org.chrishatton.crosswind.ui.presenter

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import org.chrishatton.crosswind.Crosswind
import org.chrishatton.crosswind.rx.observeOnLogicThread
import org.chrishatton.crosswind.rx.subscribeOnLogicThread
import org.chrishatton.crosswind.ui.contract.ViewContract
import org.chrishatton.crosswind.util.Nullable

abstract class Presenter<T: ViewContract>(protected val attachedViewStream : Observable<Nullable<T>>) {

    constructor( view: T ) : this( Observable.just(Nullable(view)) )

    sealed class Exception(message:String) : kotlin.Exception(message) {
        class InvalidState(message: String) : Exception(message)
    }

    sealed class State {
        object NotCreated : State()
        data class Created<T:ViewContract>(val attachedViewStream: Observable<Nullable<T>>, val isResumed: Observable<Boolean> ) : State()
    }

    private val stateSubject : BehaviorSubject<State> = BehaviorSubject.createDefault(State.NotCreated)
    val stateStream : Observable<State> = stateSubject.hide()

    private val isActiveSubject : BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    private val isActiveStream = isActiveSubject.hide()

    private val activeSubscriptions    = CompositeDisposable()
    private val presenterSubscriptions = CompositeDisposable()
    private val viewSubscriptions      = CompositeDisposable()

    fun create()  = onCreate( presenterSubscriptions )
    fun pause()   = onPause()
    fun resume()  = onResume( activeSubscriptions )
    fun destroy() = onDestroy()

    protected open fun onCreate( subscriptions: CompositeDisposable ) {

        when(stateSubject.value) {
            State.NotCreated -> stateSubject.onNext(State.Created(attachedViewStream, isActiveStream))
            else             -> throw Exception.InvalidState("")
        }

        data class ViewChange(val oldView: T?, val newView : T?)

        val safeAttachedViewStream =
                attachedViewStream
                        .startWith( Nullable() )
                        //.onErrorReturnItem( Nullable() )
                        .share()

        val attachedViewChangesStream : Observable<ViewChange> =
                Observables.zip( safeAttachedViewStream, safeAttachedViewStream.skip(1) )
                        .map { ViewChange(it.first.value,it.second.value) }
                        //.distinctUntilChanged()

        attachedViewChangesStream
            .subscribeOnLogicThread()
            .doOnNext { Crosswind.environment.logger("View bound: $this <- $it") }
            .observeOnLogicThread()
            .subscribe { attachedViewChange ->
                attachedViewChange.oldView?.let { onViewDetached(it) }
                attachedViewChange.newView?.let { onViewAttached(it, viewSubscriptions) }
            }
            .addTo(presenterSubscriptions)
    }

    protected open fun onResume( subscriptions: CompositeDisposable ) = when(stateSubject.value) {
        State.NotCreated -> throw Exception.InvalidState("")
        else -> when(isActiveSubject.value) {
            false -> isActiveSubject.onNext(true)
            true -> throw Exception.InvalidState("")
        }
    }

    protected open fun onPause() {
        activeSubscriptions.dispose()
        when(stateSubject.value) {
            State.NotCreated -> throw Exception.InvalidState("")
            else -> when(isActiveSubject.value) {
                false -> throw Exception.InvalidState("")
                true -> isActiveSubject.onNext(false)
            }
        }
    }

    protected open fun onDestroy() {
        presenterSubscriptions.dispose()
        when(stateSubject.value) {
            is State.Created<*> -> stateSubject.onNext(State.NotCreated)
            else -> throw Exception.InvalidState("")
        }
    }

    protected open fun onViewAttached( view: T, viewSubscriptions: CompositeDisposable ) = Unit
    protected open fun onViewDetached( view: T ) = viewSubscriptions.dispose()
}
