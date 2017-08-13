package org.chrishatton.geoklient.model.request.wms

import org.chrishatton.geoklient.model.request.OpenGisRequest

abstract class WebMapServiceRequest : OpenGisRequest() {
    final override val serviceIdentifier : String = "WMS"
}
