package org.chrishatton.geobrowser.ui.view

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.app_bar_map.*
import org.chrishatton.crosswind.ui.view.PresentedActivity
import org.chrishatton.geobrowser.R
import org.chrishatton.geobrowser.ui.contract.BrowserViewContract
import org.chrishatton.geobrowser.ui.contract.LayerListViewContract
import org.chrishatton.geobrowser.ui.contract.MapViewContract
import org.chrishatton.geobrowser.ui.presenter.BrowserPresenter

class BrowserView : PresentedActivity<BrowserViewContract,BrowserPresenter>(), BrowserViewContract {

    override lateinit var layersViewContract : LayerListViewContract
    override lateinit var mapViewContract    : MapViewContract

    override fun createPresenter(): BrowserPresenter {
        return BrowserPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->
            val sydney = LatLng(-34.0, 151.0)
            map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
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