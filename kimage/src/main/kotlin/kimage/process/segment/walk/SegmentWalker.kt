package kimage.process.segment.walk

import kimage.model.segment.Segel
import kimage.process.ProgressObserver
import kimage.model.segment.Segment
import kimage.model.segment.walk.SegmentWalkResult

import java.util.HashMap

class SegmentWalker<T> {
    inline fun <reified TR : SegmentWalkResult> walkAll(
            segments                  : Set<Segment<T>>,
            delegate                  : SegmentWalkDelegate<T>,
            noinline progressObserver : ProgressObserver?
    ): Map<Segment<T>, TR> {
        val results = HashMap<Segment<T>, TR>()

        var walkResult: TR

        segments.forEachIndexed { index, segment ->
            progressObserver?.invoke(index.toFloat() / segments.size.toFloat())

            walkResult = walk(segment, delegate)!!

            results.put(segment, walkResult)
        }

        progressObserver?.invoke(1f)

        return results
    }

    inline fun <reified TR : SegmentWalkResult> walk(segment: Segment<T>, delegate: SegmentWalkDelegate<T>): TR? {
        val result: TR?

        if (delegate.shouldBeginWalkAtSegment(segment)) {
            try {
                result = TR::class.java.newInstance()
            } catch (e: Exception) {
                throw RuntimeException("Unknown walk result type: " + TR::class.java.name)
            }

            val firstStep = Step(segment, null)

            nextWalkStep(firstStep, 0, delegate)
        } else
            result = null

        return result
    }

    fun nextWalkStep(currentStep: Step<T>, walkLength: Int, delegate: SegmentWalkDelegate<T>) {
        var walkLength = walkLength
        var nextStep: Step<T>

        val adjacentSegments = currentStep.segment.adjacentSegments(Segel.NeighbourPattern.Cross)

        for (adjacentSegment in adjacentSegments) {
            if (adjacentSegment !== currentStep.segment) {
                nextStep = Step(adjacentSegment, currentStep)

                nextWalkStep(nextStep, ++walkLength, delegate)
            }
        }
    }

    class Step<T>(internal val segment: Segment<T>, internal val previousStep: Step<T>?)
}
