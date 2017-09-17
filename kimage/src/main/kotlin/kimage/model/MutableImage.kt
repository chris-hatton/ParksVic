package kimage.model

import kimage.model.pixel.Pixel

interface MutableImage<PixelType: Pixel> : Image<PixelType> {

    operator fun set( x: Int, y: Int, color: PixelType )

    fun toImmutable() : Image<PixelType> {
        return object : Image<PixelType> {
            override val width  : Int = this@MutableImage.width
            override val height : Int = this@MutableImage.height
            override fun get(x: Int, y: Int): PixelType = this@MutableImage[x, y]
        }
    }
}


