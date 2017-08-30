package geoklient.model.request.wfs

import geoklient.model.MimeType
import geoklient.model.UpdateSequence
import geoklient.model.request.wms.WebMapServiceRequest

/**
 *
 */
class GetCapabilities(
        val format          : MimeType,
        val updateSequence  : UpdateSequence? = null
) : WebFeatureServiceRequest() {
    override val requestIdentifier: String = "GetCapabilities"

    override fun collateParameters(parameters: MutableList<Pair<String, String>>) {
        super.collateParameters(parameters)
        parameters.apply {
            add("FORMAT" to format.toString())
            updateSequence?.let { "UPDATESEQUENCE" to it }
        }
    }
}