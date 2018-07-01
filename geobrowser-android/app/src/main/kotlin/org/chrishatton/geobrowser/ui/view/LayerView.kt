package org.chrishatton.geobrowser.ui.view

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import opengis.model.app.MapViewLayer
import org.chrishatton.geobrowser.rx.RxRecyclerAdapter
import org.chrishatton.geobrowser.R
import org.chrishatton.geobrowser.ui.contract.LayerViewContract
import com.jakewharton.rxbinding2.widget.*
import kotlinx.android.synthetic.main.map_layer_feature.view.*
import kotlinx.android.synthetic.main.map_layer_tile.view.*

sealed class LayerView<T: MapViewLayer>(layoutInflater: LayoutInflater, layoutId: Int, viewGroup: ViewGroup?)
    : RxRecyclerAdapter.ViewHolder<T>(layoutInflater.inflate(layoutId,viewGroup,false)), LayerViewContract {

    class Feature(layoutInflater: LayoutInflater, viewGroup: ViewGroup?) : LayerView<MapViewLayer.Feature> (layoutInflater, R.layout.map_layer_feature,viewGroup) {
        override val isSelectedConsumer : Consumer<in Boolean>      by lazy { itemView.checkedFeatureTextView.checked() }
        override val title              : Consumer<in CharSequence> by lazy { itemView.checkedFeatureTextView.text() }
        override val info               : Consumer<in CharSequence> by lazy { itemView.featureInfoTextView.text() }
        override val isSelectedStream   : Observable<Boolean>       by lazy { itemView.checkedFeatureTextView.checkedChanges() }
    }

    class Tile(layoutInflater: LayoutInflater, viewGroup:ViewGroup?) : LayerView<MapViewLayer.Tile> (layoutInflater,R.layout.map_layer_tile,viewGroup) {
        override val isSelectedConsumer : Consumer<in Boolean>      by lazy { itemView.checkedTileTextView.checked() }
        override val title              : Consumer<in CharSequence> by lazy { itemView.checkedTileTextView.text() }
        override val info               : Consumer<in CharSequence> by lazy { itemView.tileInfoTextView.text() }
        override val isSelectedStream   : Observable<Boolean>       by lazy { itemView.checkedTileTextView.checkedChanges() }
    }
}
