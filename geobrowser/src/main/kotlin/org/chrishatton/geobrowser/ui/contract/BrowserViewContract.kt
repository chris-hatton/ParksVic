package org.chrishatton.geobrowser.ui.contract

import org.chrishatton.crosswind.ui.contract.ViewContract

interface BrowserViewContract : ViewContract {
    val layersViewContract : LayerListViewContract
    val mapViewContract    : MapViewContract
}