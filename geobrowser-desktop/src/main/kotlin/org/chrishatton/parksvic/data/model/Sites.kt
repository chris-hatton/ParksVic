package org.chrishatton.geobrowser.data.model

import com.lynden.gmapsfx.javascript.`object`.LatLong

val Site.latLong : LatLong get() = LatLong( this.latitude ?: 0.0, this.longitude ?: 0.0 )