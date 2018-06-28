package org.chrishatton.geobrowser

import android.app.Application
import org.chrishatton.crosswind.Crosswind
import org.chrishatton.crosswind.androidEnvironment

class GeoBrowserApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Crosswind.initialize( environment = Crosswind.androidEnvironment )
    }
}