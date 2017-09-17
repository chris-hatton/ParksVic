package kimage.model.segment

import kimage.model.Image
import kimage.model.pixel.Pixel
import kimage.model.Point
import kotlin.properties.Delegates

/**
 * 'Segel' is to Segment as 'Pixel' is to Picture.
 * It is a Segment-Element.
 */
class Segel<T>(val point: Point, segment: Segment<T>? = null ) : Pixel {

    var image: Image<Segel<T>>? = null

    var segment: Segment<T>? by Delegates.observable(segment) { _, old, new ->
        if (old != new) {
            old?.remove(this)
            new?.add(this)
        }
    }

    enum class NeighbourPattern(val offsets: Array<Point> ) {
        NorthAndWest(arrayOf(
            Point(-1 ,  0),
            Point( 0 , -1)
        )),
        Cross(arrayOf(
            Point( 1,  0),
            Point( 0,  1),
            Point(-1,  0),
            Point( 0, -1)
        )),
        Square(arrayOf(
            Point(-1, -1),
            Point( 1, -1),
            Point(-1,  1),
            Point( 1,  1),
            Point( 1,  0),
            Point( 0,  1),
            Point(-1,  0),
            Point( 0, -1)
        ))
    }

    fun getNeighbours(pattern: NeighbourPattern): Set<Segel<T>> {

        val parentImage = this.image ?: throw Exception()

        val neighbourList = pattern.offsets.mapNotNull { offset -> parentImage.getOrNull( offset + point ) }

        return neighbourList.toSet()
    }
}
