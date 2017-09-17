package kimage

import javafx.scene.paint.Color
import kimage.model.pixel.Pixel
import kimage.model.pixel.RGB

data class JFXColorPixel( var color: Color) : Pixel {
    constructor( rgb: RGB) : this( color = rgb.toJFXColor() )
}