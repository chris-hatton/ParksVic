package opengis.model.transport.xml.ows

import org.simpleframework.xml.Element

/**
 * Model for the Service Meta-data document generated in response to OpenGIS 'GetCapabilties'
 * requests.
 */
open class CapabilitiesBaseType(
        @field:Element(name="ServiceIdentification", required = false) var serviceIdentification : ServiceIdentification? = null,
        @field:Element(name="ServiceProvider", required = false      ) var serviceProvider       : ServiceProvider?       = null,
        @field:Element(name="OperationsMetadata", required = false ) var operationsMetadata    : OperationsMetadata?    = null
)
