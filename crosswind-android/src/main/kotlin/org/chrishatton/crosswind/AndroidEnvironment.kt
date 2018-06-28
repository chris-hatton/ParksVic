package org.chrishatton.crosswind

import android.os.Looper
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers

val Crosswind.androidEnvironment : Crosswind.Environment get() = Crosswind.Environment(
    uiScheduler  = AndroidSchedulers.mainThread(),
    logger       = { message -> Log.d("Crosswind",message) },
    isOnUiThread = { Looper.getMainLooper().thread == Thread.currentThread() }
)

