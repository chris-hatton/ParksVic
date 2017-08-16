import geojson.GeoJsonObject
import geojson.geometry.impl.LineString
import geojson.geometry.impl.Point
import geojson.geometry.impl.Polygon
import geojson.gson.GeoJsonObjectType
import junit.framework.AssertionFailedError
import org.junit.Assert
import org.junit.Test

/**
 * Created by Chris on 14/08/2017.
 */

class TestGeoJson {

    @Test
    fun testCommutePoint() {
        val point : Point = Point( latitude = 1.0, longitude = 1.0 )
        testCommuteJson( geoJsonObject = point )
    }

    @Test
    fun testCommuteLineString() {
        val lineString : LineString = LineString.fromVertexPairs(
                (0.0 to 0.0),
                (1.0 to 0.0),
                (1.0 to 1.0)
        )
        testCommuteJson( geoJsonObject = lineString )
    }

    @Test
    fun testCommutePolygon() {
        val polygon : Polygon = Polygon.fromVertexPairs(
                (0.0 to 0.0),
                (1.0 to 0.0),
                (1.0 to 1.0),
                (0.0 to 1.0)
        )
        testCommuteJson( geoJsonObject = polygon )
    }

    inline private fun <reified T:GeoJsonObject> testCommuteJson(geoJsonObject: T ) {
        val name = GeoJsonObjectType.forObject(geoJsonObject).name
        println("Converting $name to JSON...")
        val geoJsonText = geoJsonObject.toJson()
        println(geoJsonText)
        println("Converting JSON to $name...")
        val geoJsonObjectOut : T = GeoJsonObject.fromJson<T>( geoJsonText )
        println("Comparing...")
        Assert.assertEquals( geoJsonObject, geoJsonObjectOut )
        println("Success")
    }
}