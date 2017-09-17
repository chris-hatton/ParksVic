package kimage.model.pixel

data class RGB(val red: Double, val green: Double, val blue: Double) : Pixel {

    constructor(red: Int, green: Int, blue: Int) : this( red/255.0, green/255.0, blue/255.0 )

    companion object {
        val black : RGB = RGB(0.0, 0.0, 0.0)
        val white : RGB = RGB(1.0, 1.0, 1.0)
    }

    val rInt : Int get() = (red   * 255.0).toInt()
    val gInt : Int get() = (green * 255.0).toInt()
    val bInt : Int get() = (blue  * 255.0).toInt()

    infix operator fun minus( other: RGB) : RGB {
        return RGB(
                red = Math.max(0.0, red - other.red),
                green = Math.max(0.0, green - other.green),
                blue = Math.max(0.0, blue - other.blue)
        )
    }

    infix operator fun plus( other: RGB) : RGB {
        return RGB(
                red   = Math.min(1.0, red   + other.red   ),
                green = Math.min(1.0, green + other.green ),
                blue  = Math.min(1.0, blue  + other.blue  )
        )
    }

    infix operator fun times( other: RGB) : RGB {
        return RGB(
                red   = (red * other.red),
                green = (green * other.green),
                blue  = (blue * other.blue)
        )
    }

    infix operator fun div( other: RGB) : RGB {
        return RGB(
                red   = Math.min(1.0, red / other.red),
                green = Math.min(1.0, green / other.green),
                blue  = Math.min(1.0, blue / other.blue)
        )
    }
}