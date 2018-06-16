package org.chrishatton.geobrowser.ui.view

import android.app.Activity
import android.view.ViewGroup
import opengis.model.app.MapViewLayer
import org.chrishatton.geobrowser.rx.RxRecyclerAdapter
import org.chrishatton.geobrowser.R

sealed class LayerView<T: MapViewLayer>(activity: Activity, layoutId: Int, viewGroup: ViewGroup?)
    : RxRecyclerAdapter.ViewHolder<T>(activity.layoutInflater.inflate(layoutId,viewGroup,false)) {

    class Feature(view:LayersView, viewGroup: ViewGroup?) : LayerView<MapViewLayer.Feature> (view, R.layout.map_layer_feature,viewGroup) {

    }

    class Tile(view:LayersView, viewGroup:ViewGroup?) : LayerView<MapViewLayer.Tile> (view,R.layout.map_layer_tile,viewGroup) {

    }
}
