package opengis.process

import android.util.Xml
import okhttp3.OkHttpClient
import opengis.model.app.OpenGisHttpServer
import opengis.process.deserialize.OpenGisResponseDeserializer
import opengis.process.deserialize.impl.BitmapDeserializer
import opengis.process.okhttp.OkHttpOpenGisClient
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

/**
 * Created by Chris on 23/09/2017.
 */
class AndroidOpenGisClient private constructor(private val okHttpOpenGisClient : OkHttpOpenGisClient) : OpenGisRequestProcessor by okHttpOpenGisClient {

    constructor(server : OpenGisHttpServer) : this(create(server))

    companion object {

        private val sharedHttpClient : OkHttpClient by lazy { OkHttpClient.Builder().build() }

        private fun create(server : OpenGisHttpServer, httpClient: OkHttpClient = sharedHttpClient ) : OkHttpOpenGisClient {

            val parserFactory = object : XmlPullParserFactory() {
                override fun newPullParser(): XmlPullParser = Xml.newPullParser()
            }

            val androidDeserializer = BitmapDeserializer()
                    .then(OpenGisResponseDeserializer.createDefault(parserFactory))

            return OkHttpOpenGisClient(
                server               = server,
                okHttpClient         = httpClient,
                responseDeserializer = androidDeserializer
            )
        }
    }
}
