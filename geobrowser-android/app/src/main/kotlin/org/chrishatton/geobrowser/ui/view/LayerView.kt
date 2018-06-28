package org.chrishatton.geobrowser.ui.view

import android.app.Activity
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import opengis.model.app.MapViewLayer
import org.chrishatton.geobrowser.rx.RxRecyclerAdapter
import org.chrishatton.geobrowser.R
import org.chrishatton.geobrowser.ui.contract.LayerViewContract
import kotlinx.android.synthetic.main.map_layer_feature.*
import com.jakewharton.rxbinding2.widget.*
import kotlinx.android.synthetic.main.map_layer_tile.*

sealed class LayerView<T: MapViewLayer>(activity: Activity, layoutId: Int, viewGroup: ViewGroup?)
    : RxRecyclerAdapter.ViewHolder<T>(activity.layoutInflater.inflate(layoutId,viewGroup,false)), LayerViewContract {

    class Feature(view:LayersView, viewGroup: ViewGroup?) : LayerView<MapViewLayer.Feature> (view, R.layout.map_layer_feature,viewGroup) {
        override val title: Consumer<in CharSequence> by lazy {  view.checkedFeatureTextView.text() }
        override val isSelectedStream: Observable<Boolean> by lazy {  view.checkedFeatureTextView.checkedChanges() }
    }

    class Tile(view:LayersView, viewGroup:ViewGroup?) : LayerView<MapViewLayer.Tile> (view,R.layout.map_layer_tile,viewGroup) {
        override val title: Consumer<in CharSequence> by lazy { view.checkedTileTextView.text() }
        override val isSelectedStream: Observable<Boolean> by lazy { view.checkedTileTextView.checkedChanges() }
    }
}
