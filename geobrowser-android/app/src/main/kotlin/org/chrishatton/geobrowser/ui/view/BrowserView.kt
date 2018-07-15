package org.chrishatton.geobrowser.ui.view

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_browser.*
import kotlinx.android.synthetic.main.app_bar_map.*
import opengis.model.app.OpenGisHttpServer
import opengis.process.AndroidOpenGisClient
import opengis.process.ServerListLoader
import opengis.process.load
import org.chrishatton.crosswind.ui.view.PresentedActivity
import org.chrishatton.geobrowser.R
import org.chrishatton.geobrowser.ui.contract.BrowserViewContract
import org.chrishatton.geobrowser.ui.contract.LayerListViewContract
import org.chrishatton.geobrowser.ui.contract.MapViewContract
import org.chrishatton.geobrowser.ui.presenter.BrowserPresenter

class BrowserView : PresentedActivity<BrowserViewContract,BrowserPresenter>(), BrowserViewContract {

    override val serverList: Iterable<OpenGisHttpServer> by lazy {
            ServerListLoader.load( context = this.applicationContext, resource = R.raw.server_list )
    }

    override lateinit var layersViewContract : LayerListViewContract
    override lateinit var mapViewContract    : MapViewContract

    override fun createPresenter(): BrowserPresenter {

        val layerListFragment : LayerListView = layer_list_fragment as LayerListView
        val mapFragment       : MapView       = map_fragment        as MapView

        return BrowserPresenter(
            layerListPresenter = layerListFragment.presenter,
            mapPresenter       = mapFragment.presenter,
            clientProvider     = ::AndroidOpenGisClient,
            view               = this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browser)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
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
}