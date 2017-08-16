package org.chrishatton.parksvic.process.wms

import android.util.Log
import com.google.android.gms.maps.model.UrlTileProvider
import okhttp3.HttpUrl
import geojson.BoundingBox
import geojson.geometry.impl.Point
import geojson.reference.CRS
import org.chrishatton.geoklient.model.request.wms.GetMap
import java.net.URL

// Construct with tile size in pixels, normally 256, see parent class.
class WmsTileProvider( val baseUrl: HttpUrl, val styledLayers: List<GetMap.StyledLayer>, x: Int = 256, y: Int = 256) : UrlTileProvider(x, y) {

    companion object {
        val lngSpan : Double = 360.0
        val latSpan : Double = 180.0
    }

    override fun getTileUrl(x: Int, y: Int, zoom: Int): URL {

        val boundingBox : BoundingBox = getBoundingBox(x, y, zoom)

        //Log.d("TILE",boundingBox.toString())

        val getMap : GetMap = GetMap(
                styledLayers = styledLayers,
                reference    = CRS.Layer( nameSpace = CRS.Namespace.EPSG.name, name = "3857"),
                boundingBox  = boundingBox,
                transparent  = true
        )

        val url = getMap.buildUrl( baseUrl )

        return url.url()
    }

    // Return a web Mercator bounding box given tile x/y indexes and a zoom
    // level.
    private fun getBoundingBox(x: Int, y: Int, zoom: Int): BoundingBox {

        Log.d("TILE", "x $x, y $y, zoom $zoom" )

        val tileCount : Int = Math.pow( 2.0, zoom.toDouble() ).toInt()

        val tileSpanLng : Double = lngSpan / tileCount
        val tileSpanLat : Double = latSpan / tileCount

        val west  : Double =  x    * tileSpanLng - (lngSpan/2)
        val east  : Double = (x+1) * tileSpanLng - (lngSpan/2)

        val north : Double = (latSpan/2) - ( y    * tileSpanLat)
        val south : Double = (latSpan/2) - ((y+1) * tileSpanLat)

        Log.d("TILE", "latitude $north, south $south" )

        return BoundingBox(
                southWest = Point(longitude = west, latitude = south),
                northEast = Point(longitude = east, latitude = north)
            )
    }
}