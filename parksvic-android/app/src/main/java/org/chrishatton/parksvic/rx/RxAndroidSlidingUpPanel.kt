package org.chrishatton.parksvic.rx

import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener as PanelSlideListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState as PanelState
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable

/**
 * Observable of the [SlidingUpPanelLayout]s panel state.
 */
fun SlidingUpPanelLayout.panelState() : Observable<PanelState> {
    return this.createPanelSlideListenerObservable<PanelState> { emitter ->
        emitter.onNext( this.panelState )
        object : PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {}
            override fun onPanelStateChanged( panel: View?, previousState: PanelState?, newState: PanelState? ) {
                if( newState == null || newState == previousState ) { return }
                emitter.onNext( newState )
            }
        }
    }
}

/**
 * Observable of the height of the part of the main panel which is overlapped by the sliding panel.
 * One use of this is to add padding to the main panel, so that important content remains in the
 * visible portion while the sliding panel is raised.
 */
fun SlidingUpPanelLayout.slidePanelOverlapHeight() : Observable<Int> {
    return this.createPanelSlideListenerObservable<Int> { emitter ->
        object : PanelSlideListener {
            override fun onPanelStateChanged( panel: View?, previousState: PanelState?, newState: PanelState? ) {}
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                if(panel == null) { return }
                val mainPanelHeight = panel.height - this@slidePanelOverlapHeight.panelHeight
                val visibleHeight : Int = (mainPanelHeight.toFloat() * slideOffset).toInt()
                emitter.onNext( visibleHeight )
            }
        }
    }
}

/**
 * Utility function to de-duplicate the process for creating Observables based on SlidingUpPanelLayout.PanelSlideListener.
 */
private fun <T> SlidingUpPanelLayout.createPanelSlideListenerObservable(listenerCreator: (ObservableEmitter<T>)->PanelSlideListener ) : Observable<T> {
    return Observable.create { emitter ->
        var isDisposed : Boolean = false
        val listener : PanelSlideListener = listenerCreator(emitter)
        // Register the listener.
        this.addPanelSlideListener( listener )
        val disposable = object :Disposable {
            override fun isDisposed(): Boolean { return isDisposed }
            override fun dispose() {
                isDisposed = true
                // When the subscription is disposed, unregister the listener.
                this@createPanelSlideListenerObservable.removePanelSlideListener(listener)
            }
        }
        emitter.setDisposable(disposable)
    }
}