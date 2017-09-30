package opengis.process

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import opengis.process.okhttp.OkHttpOpenGisClient

/**
 * Created by Chris on 23/09/2017.
 */
class AndroidOpenGisClient private constructor(private val okHttpOpenGisClient : OkHttpOpenGisClient<Bitmap>) : OpenGisClient by okHttpOpenGisClient {

    constructor(baseUrl : HttpUrl) : this(create(baseUrl))

    companion object {

        val sharedHttpClient : OkHttpClient by lazy { OkHttpClient.Builder().build() }

        private fun create(baseUrl : HttpUrl, httpClient: OkHttpClient = sharedHttpClient ) : OkHttpOpenGisClient<Bitmap> {

            val bitmapDeserializer = { bytes:ByteArray ->
                BitmapFactory.decodeByteArray( bytes, 0, bytes.size )
            }

            val defaultDeserializer = DefaultOpenGisResponseDeserializer(
                imageDeserializer = bitmapDeserializer,
                imageClass = Bitmap::class
            )
            return OkHttpOpenGisClient(
                baseUrl              = baseUrl,
                okHttpClient         = httpClient,
                responseDeserializer = defaultDeserializer
            )
        }
    }
}
