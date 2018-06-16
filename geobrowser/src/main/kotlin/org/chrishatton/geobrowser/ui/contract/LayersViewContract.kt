package org.chrishatton.geobrowser.ui.contract

import io.reactivex.functions.Consumer
import org.chrishatton.crosswind.ui.contract.ViewContract
import org.chrishatton.geobrowser.ui.presenter.LayerPresenter

/**
 * Created by Chris on 19/01/2018.
 */
interface LayersViewContract : ViewContract {
    var layers : Consumer<Iterable<LayerPresenter>>
}