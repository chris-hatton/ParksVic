package opengis.test

import org.junit.Test

interface WmsTests {
    @Test fun testGetCapabilities()
    @Test fun testGetFeatureInfo()
    @Test fun testGetMap()
}