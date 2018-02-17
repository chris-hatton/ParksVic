package org.chrishatton.geobrowser.ui.contract

import io.reactivex.functions.Consumer
import opengis.ui.model.MapViewable
import java.util.SortedSet

/**
 * Created by Chris on 19/01/2018.
 */
interface LayersViewContract {
    var layers : Consumer<SortedSet<MapViewable>>
}