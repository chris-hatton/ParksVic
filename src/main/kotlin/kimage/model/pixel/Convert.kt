package kimage.model.pixel

fun RGB.toHSV() : HSV {
    val hsv = FloatArray(3, { 0f } )
    java.awt.Color.RGBtoHSB(rInt,gInt,bInt,hsv)
    return HSV(hsv[0].toDouble(), hsv[1].toDouble(), hsv[2].toDouble())
}

fun HSV.toRGB() : RGB {
    val packedRGB = java.awt.Color.HSBtoRGB(hue.toFloat(), saturation.toFloat(), value.toFloat())
    val color = java.awt.Color(packedRGB)
    return color.toRGB()
}

fun java.awt.Color.toRGB() : RGB {
    val rgb = FloatArray(3, { 0f } )
    getRGBColorComponents(rgb)
    return RGB(rgb[0].toDouble(), rgb[1].toDouble(), rgb[2].toDouble())
}
