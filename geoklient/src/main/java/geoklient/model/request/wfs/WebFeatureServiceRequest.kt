package org.chrishatton.geoklient.model.request.wfs

import org.chrishatton.geoklient.model.request.OpenGisRequest

abstract class WebFeatureServiceRequest : OpenGisRequest() {

    final override val serviceIdentifier : String = "WFS"
}