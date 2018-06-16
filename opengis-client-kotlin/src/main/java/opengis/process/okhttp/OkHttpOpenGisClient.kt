package opengis.process.okhttp

import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Response
import opengis.model.app.OpenGisHttpServer
import opengis.model.app.request.OpenGisRequest
import opengis.process.*
import opengis.process.deserialize.OpenGisResponseDeserializer
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL

/**
 * Created by Chris on 21/09/2017.
 */
class OkHttpOpenGisClient(
        server                           : OpenGisHttpServer,
        private val okHttpClient         : OkHttpClient = OkHttpClient(),
        private val responseDeserializer : OpenGisResponseDeserializer = OpenGisResponseDeserializer.createDefault()
    ) : OpenGisHttpClient(server), OpenGisResponseDeserializer by responseDeserializer {

    override fun <Result : Any> getBytes(url: URL, request: OpenGisRequest<Result>, callback: Callback<InputStream>) {
        val httpUrl = HttpUrl.get(url)!!
        val call = okHttpClient.newCall( baseUrl = httpUrl, openGisRequest = request )
        call.enqueue(callback){ it }
    }
}