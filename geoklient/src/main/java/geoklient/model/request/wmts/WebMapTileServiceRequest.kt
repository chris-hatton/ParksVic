package org.chrishatton.geoklient.model.request.wmts

import org.chrishatton.geoklient.model.request.OpenGisRequest

/**
 * Created by Chris on 10/08/2017.
 */
abstract class WebMapTileServiceRequest : OpenGisRequest() {

    final override val serviceIdentifier : String = "WMTS"
}