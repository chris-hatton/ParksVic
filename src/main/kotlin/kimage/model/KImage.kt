package kimage.model

import kimage.model.pixel.Pixel

private typealias MutableList2D<T> = MutableList<MutableList<T>>

/**
 * Simple Kotlin implementation of a MutableImage
 */
class KImage<PixelColor: Pixel> constructor(
        override val width     : Int,
        override val height    : Int,
        init: (Point)->PixelColor
) : MutableImage<PixelColor> {

    private val imageData : MutableList2D<PixelColor>

    init {
        imageData = MutableList(height) { y ->
            MutableList(width, { x->
                init(Point(x,y))
            })
        }
    }

    override fun set(x: Int, y: Int, color: PixelColor) {
        imageData[y][x] = color
    }

    override fun get(x: Int, y: Int): PixelColor = imageData[y][x]
}