package kimage.model.segment

import kimage.model.Image
import kimage.process.segment.segments

fun <T> Image<Segel<T>>.reduceSegments( merge : (Segment<T>,Segment<T>)->Segment<T>? ) : Set<Segment<T>> {

    val segments : Set<Segment<T>> = this.segments()

    for( segment in segments ) {
        if( segment.isEmpty() ) { continue }
        val adjacentSegments = segment.adjacentSegments( Segel.NeighbourPattern.NorthAndWest )

        adjacentSegments.fold( segment ) { segment,matchingAdjacentSegment ->
            val mergedSegment : Segment<T>? = merge(segment,matchingAdjacentSegment)
            if( mergedSegment == null ) {
                segment
            } else {
                segment                .replace(mergedSegment)
                matchingAdjacentSegment.replace(mergedSegment)
                mergedSegment
            }
        }
    }

    return segments
}

fun <T> Image<Segel<T>>.reduceSegmentsUntilStable(merge: (Segment<T>, Segment<T>)->Segment<T>?) : Set<Segment<T>> {

    var segments : Set<Segment<T>>
    var count : Int? = null

    do {
        val countBefore = count
        segments = reduceSegments( merge )
        count = segments.count()

    } while( countBefore?.let { it != countBefore } ?: true )

    return segments
}

fun <T> dataReduce( a: Segment<T>, b: Segment<T> ) {}