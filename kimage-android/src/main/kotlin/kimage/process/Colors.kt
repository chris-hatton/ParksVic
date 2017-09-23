package kimage.process

import android.graphics.Color
import kimage.model.pixel.RGB

/**
 * Created by Chris on 17/06/2017.
 */
fun colorToRGB( color: Int ) : RGB {
    return RGB(
            red   = Color.red  ( color ),
            green = Color.green( color ),
            blue  = Color.blue ( color )
    )
}
