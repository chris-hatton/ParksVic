package org.chrishatton.parksvic.data.process

import io.reactivex.Observable
import okhttp3.*
import opengis.model.request.OpenGisRequest
import opengis.process.okhttp.newCall
import java.io.IOException

/**
 * Created by Chris on 17/09/2017.
 */
inline fun <reified Result> OkHttpClient.execute(baseUrl: HttpUrl, openGisRequest: OpenGisRequest<Result>) : Observable<Result> {
    return Observable.create { emitter ->
        val callback : Callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) = emitter.onError(e)
            override fun onResponse(call: Call, response: Response) {

                val byteStream = response.body()!!.byteStream()


            }
        }

        this.newCall(baseUrl, openGisRequest).enqueue(callback)
    }
}