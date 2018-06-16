package opengis.process

import android.content.Context
import opengis.model.app.OpenGisHttpServer

/**
 * Created by Chris on 25/02/2018.
 */
fun ServerListLoader.load(context: Context, resource: Int) : Iterable<OpenGisHttpServer> {
    val inputStream = context.resources.openRawResource(resource)
    return ServerListLoader.deserializeServerList(inputStream)
}