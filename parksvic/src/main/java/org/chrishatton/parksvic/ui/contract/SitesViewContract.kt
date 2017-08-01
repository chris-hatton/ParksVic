package org.chrishatton.parksvic.ui.contract

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import org.chrishatton.crosswind.ui.contract.ViewContract
import org.chrishatton.geojson.BoundingBox
import org.chrishatton.parksvic.data.model.Site


interface SitesViewContract : ViewContract {

    val viewportBoundingBoxes : Observable<Array<BoundingBox>>

    val viewportSitesConsumer : Consumer<Array<Site>>
}