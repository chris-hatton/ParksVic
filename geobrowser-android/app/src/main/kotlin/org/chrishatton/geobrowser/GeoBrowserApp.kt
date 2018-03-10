package org.chrishatton.geobrowser

import android.app.Application
import org.chrishatton.crosswind.androidEnvironment
import org.chrishatton.crosswind.environment

class GeoBrowserApp : Application() {
    override fun onCreate() {
        super.onCreate()
        environment = androidEnvironment
    }
}