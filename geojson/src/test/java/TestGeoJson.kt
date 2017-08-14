import geojson.Position
import geojson.geometry.Polygon
import org.junit.Test

/**
 * Created by Chris on 14/08/2017.
 */

class TestGeoJson {

    @Test
    fun testIt() {
        println("Yo!!")

        val poly : Polygon = Polygon.fromVertexPairs(
                (0.0 to 0.0),
                (1.0 to 0.0),
                (1.0 to 1.0),
                (0.0 to 1.0)
            )

        val polyString = poly.toJson()

        println(polyString)
    }
}