package org.chrishatton.crosswind

import android.os.Looper
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers

val androidEnvironment : Environment = Environment(
    uiScheduler  = AndroidSchedulers.mainThread(),
    logger       = { message -> Log.d("Crosswind",message) },
    isOnUiThread = { Looper.getMainLooper().thread == Thread.currentThread() }
)