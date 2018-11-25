package opengis.model.transport.xml.ows.operation

import org.simpleframework.xml.Element

/**
 * Specified in: http://schemas.opengis.net/wms/$ext.kotlin_version/capabilities_1_3_0.xsd
 *
 * Available Distributed Computing Platforms (DCPs) are listed here. At present, only HTTP is defined.
 */
data class DCPType(
    @field:Element(name="HTTP") var httpUrls: HTTP? = null
)