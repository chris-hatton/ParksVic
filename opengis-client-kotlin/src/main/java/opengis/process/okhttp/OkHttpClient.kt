package opengis.process.okhttp

import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Response
import opengis.model.app.request.OpenGisRequest
import opengis.process.Callback
import opengis.process.OpenGisRequestProcessor
import opengis.process.Outcome
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by Chris on 16/09/2017.
 */
fun <Result> OkHttpClient.newCall(baseUrl: HttpUrl, openGisRequest: OpenGisRequest<Result> ) : Call {
    val call = OpenGisRequestAdapter.urlRequest( baseUrl, openGisRequest )
    return newCall( call )
}

fun <T> Call.enqueue(callback: Callback<T>, deserialize: (InputStream)->T ) {

    val okHttpCallback = object : okhttp3.Callback {
        override fun onFailure (call: Call, e: IOException) = callback(Outcome.Error(e))
        override fun onResponse(call: Call, response: Response) {
            when( response.code() ) {
                200 -> {
                    val byteStream = response.body()?.byteStream() ?: ByteArrayInputStream(byteArrayOf())
                    val value = deserialize(byteStream)
                    callback(Outcome.Success( value ))
                }
                else -> {
                    val errorXml = response.body()!!.string()
                    println(errorXml)
                    val error = OpenGisRequestProcessor.Exception.ServerError( xmlString = errorXml )
                    callback(Outcome.Error(error))
                }
            }
        }
    }

    this.enqueue(okHttpCallback)
}

