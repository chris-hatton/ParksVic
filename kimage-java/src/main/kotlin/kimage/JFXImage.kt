package kimage

import javafx.scene.image.PixelReader
import javafx.scene.image.WritableImage
import kimage.model.Image
import kimage.model.pixel.RGB

/** A suite of functions to interface kimage with JavaFX images */

class JFXImageWrapper( image: javafx.scene.image.Image ) : Image<RGB> {

    private val pixelReader : PixelReader = image.pixelReader

    override val width  : Int = image.width .toInt()
    override val height : Int = image.height.toInt()

    override operator fun get(x: Int, y: Int): RGB {
        return pixelReader.getColor(x,y).toRGB()
    }
}

fun javafx.scene.paint.Color.toRGB() : RGB {
    return RGB(
        red   = this.red,
        green = this.green,
        blue  = this.blue
    )
}

fun RGB.toJFXColor() : javafx.scene.paint.Color {
    return javafx.scene.paint.Color.color( red, green, blue )
}

fun Image<RGB>.toJFXImage() : javafx.scene.image.Image {
    val writableImage = WritableImage(width,height)
    val pixelWriter = writableImage.pixelWriter
    for(y in 0 until height) {
        for(x in 0 until width) {
            val color : javafx.scene.paint.Color = this[x,y].toJFXColor()
            pixelWriter.setColor(x,y,color)
        }
    }
    return writableImage
}
