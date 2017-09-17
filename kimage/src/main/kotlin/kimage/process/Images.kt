package kimage.process

import kimage.model.*
import kimage.model.pixel.HSV
import kimage.model.pixel.RGB
import kimage.model.pixel.toHSV
import kimage.model.pixel.toRGB

fun Image<RGB>.toHSV() : Image<HSV> = this.map { rgb, _ -> rgb.toHSV() }
fun Image<HSV>.toRGB() : Image<RGB> = this.map { hsv, _ -> hsv.toRGB() }
