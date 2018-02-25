package org.chrishatton.geobrowser.ui.view

import android.app.Activity
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.app_bar_map.*
import opengis.process.*
import opengis.model.app.MapViewLayer
import org.chrishatton.geobrowser.R


class MapActivity : AppCompatActivity() {

    sealed class ViewHolder<T: MapViewLayer>(activity: Activity, layoutId: Int, viewGroup: ViewGroup?)
        : RecyclerView.ViewHolder(activity.layoutInflater.inflate(layoutId,viewGroup)) {

        var layer: T? = null

        class Feature(activity:Activity, viewGroup:ViewGroup?) : ViewHolder<MapViewLayer.Feature> (activity,R.layout.map_layer_feature,viewGroup)
        class Tile   (activity:Activity, viewGroup:ViewGroup?) : ViewHolder<MapViewLayer.Tile>    (activity,R.layout.map_layer_tile,   viewGroup)
    }

    sealed class Exception : kotlin.Exception() {
        object UnsupportedType : Exception()
        object UnexpectedViewType : Exception()
    }

    val layers : BehaviorSubject<List<MapViewLayer>> = BehaviorSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setSupportActionBar(toolbar)

        val servers = ServerListLoader.load( context = this, resource = R.raw.server_list )
        val clients = servers.map { AndroidOpenGisClient(it) }

        //val capabilities = clients.map { it.execute() }


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

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

        //nav_view.setNavigationItemSelectedListener(this)
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
