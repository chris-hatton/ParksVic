package kimage.process.segment.walk

interface SegmentWalkDelegate<T> {

    fun shouldBeginWalkAtSegment(startSegment: kimage.model.segment.Segment<T>): Boolean

    fun shouldWalkInto(currentStep: SegmentWalker.Step<T>, segment: kimage.model.segment.Segment<T>): Boolean {
        return true
    }

    fun didWalkInto(segmentWalk: kimage.model.segment.walk.SegmentWalk<T>, segment: kimage.model.segment.Segment<T>) {
        segmentWalk.didVisit(segment)
    }

    fun didEndWalk(segmentWalk: kimage.model.segment.walk.SegmentWalk<T>)
}
