package kimage.model

import kimage.model.pixel.Pixel
import kimage.process.ProgressObserver

/**
 * The contract for an immutable Images.
 * Defines several functions that may be performed on them.
 */
interface Image<PixelType: Pixel> {

    companion object {}

    val width  : Int
    val height : Int

    operator fun get( x: Int, y: Int ) : PixelType

    operator fun get( point: Point ) : PixelType = get( point.x, point.y )

    fun getOrNull( point: Point ) : PixelType? {
        return if( point.x in 0 until width && point.y in 0 until height ) this[point.x,point.y] else null
    }

    fun <MappedPixelType: Pixel> map(function: (pixel:PixelType, point:Point)->MappedPixelType ) : KImage<MappedPixelType> {
        return KImage(width,height) { point ->
            val sourcePixel = this[point]
            function(sourcePixel,point)
        }
    }

    fun forEach( function: (PixelType,Point)->Unit ) {
        for(y in 0 until height) {
            for(x in 0 until width) {
                val point = Point(x,y)
                function( this[point], point )
            }
        }
    }

    fun <T> fold ( initial: T, function: (current:T,pixel:PixelType,x:Int,y:Int)->T ) : T {
        var current : T = initial
        for(y in 0 until height) {
            for(x in 0 until width) {
                current = function( current, this[x,y], x, y )
            }
        }
        return current
    }

    fun mutableCopy() : MutableImage<PixelType> {
        return KImage(
                width  = this.width,
                height = this.height,
                init   = { point -> this[point] }
        )
    }

    fun immutableCopy() : Image<PixelType> {
        return this.mutableCopy().toImmutable()
    }

    fun mutableCopyScaled( factor: Double ) : MutableImage<PixelType> {
        return mutableCopyScaled(
            width  = (width  * factor).toInt(),
            height = (height * factor).toInt()
        )
    }

    fun mutableCopyScaled( width: Int, height: Int ) : MutableImage<PixelType> {

        val widthScaleFactor  : Double = width .toDouble() / this.width .toDouble()
        val heightScaleFactor : Double = height.toDouble() / this.height.toDouble()

        return KImage(
                width  = width,
                height = height,
                init   = { (x, y) -> this[ (x / widthScaleFactor).toInt(), (y / heightScaleFactor).toInt() ] }
        )
    }
}

