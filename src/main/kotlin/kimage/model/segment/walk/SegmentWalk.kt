package kimage.model.segment.walk

import kimage.model.segment.Segment

open class SegmentWalk<T> {

    internal var path: MutableList<kimage.model.segment.Segment<T>> = java.util.ArrayList<Segment<T>>()

    fun didVisit(segment: kimage.model.segment.Segment<T>) {
        path.add(segment)
    }
}
