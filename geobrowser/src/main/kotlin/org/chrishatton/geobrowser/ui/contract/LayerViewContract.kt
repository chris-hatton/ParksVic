package org.chrishatton.geobrowser.ui.contract

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import org.chrishatton.crosswind.ui.contract.ViewContract

interface LayerViewContract : ViewContract {
    val title : Consumer<in CharSequence>
    val isSelectedStream : Observable<Boolean>
}