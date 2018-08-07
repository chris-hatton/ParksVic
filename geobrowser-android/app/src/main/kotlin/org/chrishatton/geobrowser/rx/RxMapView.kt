package org.chrishatton.geobrowser.rx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import io.reactivex.Observable

fun MapView.getMap() : Observable<GoogleMap> = Observable.create { emitter ->
    this.getMapAsync { googleMap ->
        emitter.onNext(googleMap)
        emitter.onComplete()
    }
}
