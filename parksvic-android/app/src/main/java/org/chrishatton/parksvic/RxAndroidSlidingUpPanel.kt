package org.chrishatton.parksvic

import android.util.Log
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.reactivex.Observable

fun SlidingUpPanelLayout.panelState() : Observable<SlidingUpPanelLayout.PanelState> {

    return Observable.create { emitter ->

        val panelSlideListener : SlidingUpPanelLayout.PanelSlideListener = object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {

                Log.e("xyz", "slideOffset " + slideOffset + "panel " + this@panelState.panelHeight);
            }

            override fun onPanelStateChanged(
                    panel: View?,
                    previousState: SlidingUpPanelLayout.PanelState?,
                    newState: SlidingUpPanelLayout.PanelState?
            ) {
                if( newState == null || newState == previousState ) { return }
                emitter.onNext( newState )
            }
        }

        this.addPanelSlideListener( panelSlideListener )

        emitter.onNext( this.panelState )
    }
}