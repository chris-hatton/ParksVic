package org.chrishatton.geobrowser.ui.view

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.app_bar_map.*
import opengis.ui.model.MapViewLayer
import org.chrishatton.geobrowser.R

class MapActivity : AppCompatActivity() {

    val layers : BehaviorSubject<List<MapViewLayer>> = BehaviorSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        class MapViewLayerHolder(view: View) : RecyclerView.ViewHolder(view) {
            var mapViewLayer: MapViewLayer? = null
        }

        layer_list.adapter = object : RecyclerView.Adapter<MapViewLayerHolder>() {

            override fun getItemViewType(position: Int): Int = when(layers.value[position]) {
                is MapViewLayer.Feature -> 0
                is MapViewLayer.Tile<*> -> 1
            }

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MapViewLayerHolder {
                TODO()
            }

            override fun getItemCount(): Int = layers.value.size

            override fun onBindViewHolder(holder: MapViewLayerHolder?, position: Int) {
                holder?.mapViewLayer = layers.value[position]
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
