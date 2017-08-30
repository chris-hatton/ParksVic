package geoklient.model.request.wmts

import geoklient.model.MimeType
import geoklient.model.UpdateSequence
import geoklient.model.request.wms.WebMapServiceRequest

/**
 * WMTS GetCapabilities operation request
 * Section 7.2.2.1
 * http://portal.opengeospatial.org/files/?artifact_id=35326
 */
class GetCapabilities(
        val format          : MimeType,
        val updateSequence  : UpdateSequence? = null
) : WebMapTileServiceRequest() {
    override val requestIdentifier: String = "GetCapabilities"

    override fun collateParameters(parameters: MutableList<Pair<String, String>>) {
        super.collateParameters(parameters)
        parameters.apply {
            add("FORMAT" to format.toString())
            updateSequence?.let { "UPDATESEQUENCE" to it }
        }
    }
}