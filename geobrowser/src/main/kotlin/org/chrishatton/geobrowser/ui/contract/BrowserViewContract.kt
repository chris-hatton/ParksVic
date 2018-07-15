package org.chrishatton.geobrowser.ui.contract

import opengis.model.app.OpenGisHttpServer
import org.chrishatton.crosswind.ui.contract.ViewContract

interface BrowserViewContract : ViewContract {
    val layersViewContract : LayerListViewContract
    val mapViewContract    : MapViewContract

    val serverList : Iterable<OpenGisHttpServer>
}