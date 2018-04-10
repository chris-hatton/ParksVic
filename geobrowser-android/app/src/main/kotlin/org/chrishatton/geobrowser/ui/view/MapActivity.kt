package org.chrishatton.geobrowser.ui.view

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.app_bar_map.*
import kotlinx.android.synthetic.main.map_layer_feature.view.*
import opengis.model.app.MapViewLayer
import opengis.model.app.OpenGisHttpServer
import opengis.model.app.getMapViewLayers
import opengis.process.AndroidOpenGisClient
import opengis.process.Outcome
import opengis.process.ServerListLoader
import opengis.process.load
import org.chrishatton.crosswind.rx.subscribeOnUiThread
import org.chrishatton.geobrowser.R
import org.chrishatton.geobrowser.ui.model.LayerUiState
import java.util.*
import kotlin.properties.Delegates


class MapActivity : AppCompatActivity() {

    sealed class ViewHolder<T: MapViewLayer>(activity: MapActivity, layoutId: Int, viewGroup: ViewGroup?)
        : RecyclerView.ViewHolder(activity.layoutInflater.inflate(layoutId,viewGroup,false)) {

        var layer: T? by Delegates.observable(null as T?) { _,_,newLayer ->
            val layerToState : Pair<T,LayerUiState>? = newLayer?.let {
                newLayer to activity.getLayerUiState(newLayer)
            }
            this.didSetLayer(layerToState)
        }

        abstract fun didSetLayer(layerToState: Pair<T,LayerUiState>? )

        class Feature(activity:MapActivity, viewGroup:ViewGroup?) : ViewHolder<MapViewLayer.Feature> (activity,R.layout.map_layer_feature,viewGroup) {
            override fun didSetLayer(layerToState:Pair<MapViewLayer.Feature,LayerUiState>?) {
                itemView.checkedTextView.apply {
                    text      = layerToState?.let { it.first.featureType.name } ?: "Unnamed features"
                    isChecked = layerToState?.let { it.second.isSelected      } ?: false
                }
            }
        }

        class Tile(activity:MapActivity, viewGroup:ViewGroup?) : ViewHolder<MapViewLayer.Tile> (activity,R.layout.map_layer_tile,viewGroup) {

            override fun didSetLayer(layerToState:Pair<MapViewLayer.Tile,LayerUiState>?) {
                itemView.checkedTextView.apply {
                    val layer = layerToState?.first
                    text = when(layer) {
                        is MapViewLayer.Tile.Wmts -> layer.layer.name
                        is MapViewLayer.Tile.Wms  -> layer.layer.name
                        else -> "Unnamed features"
                    }
                    isChecked = layerToState?.let { it.second.isSelected      } ?: false
                }
            }
        }
    }

    sealed class Exception : kotlin.Exception() {
        object UnsupportedType : Exception()
        object UnexpectedViewType : Exception()
    }

    val layers : BehaviorSubject<List<MapViewLayer>> = BehaviorSubject.createDefault(emptyList())
    private val layerUiStates : WeakHashMap<MapViewLayer,LayerUiState> = WeakHashMap()

    private fun getLayerUiState(layer: MapViewLayer) : LayerUiState = layerUiStates[layer] ?: LayerUiState(isSelected = false).also {
        layerUiStates[layer] = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setSupportActionBar(toolbar)

        val servers = ServerListLoader.load( context = this, resource = R.raw.server_list )

        Outcome.fold<OpenGisHttpServer,Set<MapViewLayer>>(
            inputs  = servers,
            caller  = { server, callback -> server.getMapViewLayers(::AndroidOpenGisClient,callback) },
            initial = emptySet(),
            reduce  = { a,b -> a+b }
        ) { outcome ->
            val layers = when(outcome) {
                is Outcome.Success ->  outcome.result.toList()
                is Outcome.Error   -> emptyList()
            }
            runOnUiThread {
                this.layers.onNext(layers)
            }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        layer_list.layoutManager = LinearLayoutManager(this).apply {
                orientation = LinearLayoutManager.VERTICAL
            }

        //layer_list.adapter.sel

        layer_list.adapter = object : RecyclerView.Adapter<ViewHolder<*>>() {

            override fun getItemViewType(position: Int): Int = when(layers.value[position]) {
                is MapViewLayer.Feature -> 0
                is MapViewLayer.Tile    -> 1
                else -> throw Exception.UnsupportedType
            }

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder<out MapViewLayer> = when(viewType) {
                0 -> ViewHolder.Feature(this@MapActivity,viewGroup = parent)
                1 -> ViewHolder.Tile   (this@MapActivity,viewGroup = parent)
                else -> throw Exception.UnexpectedViewType
            }

            override fun getItemCount(): Int = layers.value.size

            override fun onBindViewHolder(holder: ViewHolder<*>?, position: Int) = when(holder) {
                is ViewHolder.Feature -> holder.layer = layers.value[position] as MapViewLayer.Feature
                is ViewHolder.Tile    -> holder.layer = layers.value[position] as MapViewLayer.Tile
                else -> throw Exception.UnexpectedViewType
            }
        }

        layers
            .subscribeOnUiThread()
            .subscribe { _ ->
                layer_list.adapter.notifyDataSetChanged()
            }

//        nav_view.setNavigationItemSelectedListener { item ->
//            val currentlySelected = selec
//            item.isChecked
//        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //drawer_layout.closeDrawer(GravityCompat.START)


}
