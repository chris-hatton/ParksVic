package kimage.model.segment

import kimage.model.Point
import java.util.*

class Segment<T>(val data: T, elements: Set<Segel<T>> = emptySet() ) {

    private var segels: MutableSet<Segel<T>> = HashSet()

    init {
        elements.forEach( this::add )
    }

    val size : Int get() { return segels.count() }

    fun add(segel: Segel<T>) {
        if (!segels.contains(segel)) {
            segels.add(segel)
            segel.segment = this
        }
    }

    fun remove(segel: Segel<T>) {
        segels.remove(segel)
    }

    fun isEmpty() : Boolean = segels.isEmpty()

//    /*
//    fun adjacentSegments() : Map<Segment<T>,Int> {
//        return this.segels
//                .neighbours( pattern = Segel.NeighbourPattern.Cross )
//                .filter { it.segment != null }
//                .groupBy { segel -> segel.segment!! }
//                .mapValues { (_,segels) -> segels.count() }
//    }
//    */
//
//    fun mergeWith( segment: Segment<T>, mergeBy: (a:T,b:T)->T ) : Segment<T> {
//        return Segment(
//                data     = mergeBy( this.data, segment.data ),
//                elements = this.segels + segment.segels
//            )
//    }

    fun replace( segment: Segment<T> ) {
        if( segment === this ) {
            return
        }

        val segels = HashSet(segels)
        segels.forEach { segel ->
            segel.segment = segment
        }
    }

    fun neighbours( pattern: Segel.NeighbourPattern ) : Set<Segel<T>> {
        val neighbours = HashSet<Segel<T>>()
        segels.forEach { segel ->
            segel.getNeighbours( pattern ).forEach { candidate ->
                if( candidate.segment != this ) {
                    neighbours.add(candidate)
                }
            }
        }
        return Collections.unmodifiableSet(neighbours)
    }

    fun adjacentSegments( pattern: Segel.NeighbourPattern ) : Set<Segment<T>> {
        return neighbours(pattern)
                .mapNotNull { it.segment }
                .toSet()
    }

    fun countedAdjacentSegments( pattern: Segel.NeighbourPattern ) : Map<Segment<T>,Int> {
        return neighbours(pattern)
                .filter { it.segment != null }
                .groupBy { it.segment!! }
                .mapValues { (_,segels) -> segels.count() }
    }

    fun centroid() : Point {
        val segels = segels
        val totalPoint : Point = segels.fold(Point(0,0)) { point, segel -> segel.point + point }
        val segelCount = segels.count()
        return Point( totalPoint.x / segelCount, totalPoint.y / segelCount )
    }

    data class Direction( val angle: Double, val magnitude: Double ) {
        fun offset() : Point {
            return Point( x = (Math.cos(angle) * magnitude).toInt(), y = (Math.sin(angle) * magnitude).toInt())
        }
    }

    fun direction() : Direction {
        val segels = segels
        val squareLength : Int = Math.sqrt(segels.count().toDouble()).toInt()
        val north = Point( 0,-1 )
        val west  = Point(-1, 0 )
        var vertical   : Int = segels.fold( 0 ) { count,segel -> count + if(segel.image?.getOrNull(segel.point + north)?.segment == this) 1 else 0 }
        var horizontal : Int = segels.fold( 0 ) { count,segel -> count + if(segel.image?.getOrNull(segel.point + west )?.segment == this) 1 else 0 }

        vertical   -= squareLength
        horizontal -= squareLength
        val base = Math.min(horizontal,vertical)
        vertical += base
        horizontal += base

        val angle     = Math.atan2(vertical.toDouble(),horizontal.toDouble())
        val magnitude = Math.sqrt( Math.pow(vertical.toDouble(),2.0) + Math.pow(horizontal.toDouble(),2.0) ) * 5

        return Direction( angle, magnitude )
    }
}

/**
 * Gets all neighbouring elements of this set of segment-elements.
 */
/*
fun <T> Set<Segel<T>>.neighbours( pattern: Segel.NeighbourPattern ) : Set<Segel<T>> {

    val allNeighbours = this.fold( initial = HashSet<Segel<T>>() ) { allNeighbours,segel ->
        val immediateNeighbours : Set<Segel<T>> = segel.getNeighbours(pattern)
        allNeighbours.addAll(immediateNeighbours)
        allNeighbours
    }

    return allNeighbours - this
}
*/