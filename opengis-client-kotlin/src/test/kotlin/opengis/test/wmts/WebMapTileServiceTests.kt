package opengis.test.wmts

import opengis.model.MimeType
import opengis.model.request.wmts.GetCapabilities
import opengis.process.okhttp.newCall
import opengis.test.OpenGisTests
import org.junit.Ignore
import org.junit.Test

/**
 * Created by Chris on 19/09/2017.
 */
class WebMapTileServiceTests : OpenGisTests() {
    @Ignore("Not yet implemented")
    @Test
    fun testGetCapabilities() {
        val request = GetCapabilities( format = MimeType.JSON, updateSequence = null )
        httpClient.newCall("")
    }

    @Ignore("Not yet implemented")
    @Test
    fun testGetFeatureInfo() {

    }

    @Ignore("Not yet implemented")
    @Test
    fun testGetTile() {

    }
}