package kimage.model.plot

import kimage.model.MutableImage
import kimage.model.pixel.Pixel

/**
 * Implementation of Bresenham's line algorithm: https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
 * Ported from: http://tech-algorithm.com/articles/drawing-line-using-bresenham-algorithm/
 */
fun <PixelType: Pixel> MutableImage<PixelType>.drawBresenhamLine( x: Int, y: Int, x2: Int, y2: Int, color: PixelType) {
    var x = x
    var y = y
    val w = x2 - x
    val h = y2 - y
    var dx1 = 0
    var dy1 = 0
    var dx2 = 0
    var dy2 = 0
    if (w < 0) dx1 = -1 else if (w > 0) dx1 = 1
    if (h < 0) dy1 = -1 else if (h > 0) dy1 = 1
    if (w < 0) dx2 = -1 else if (w > 0) dx2 = 1
    var longest = Math.abs(w)
    var shortest = Math.abs(h)
    if (longest <= shortest) {
        longest = Math.abs(h)
        shortest = Math.abs(w)
        if (h < 0) dy2 = -1 else if (h > 0) dy2 = 1
        dx2 = 0
    }
    var numerator = longest shr 1
    for (i in 0..longest) {
        this[x, y] = color
        numerator += shortest
        if (numerator >= longest) {
            numerator -= longest
            x += dx1
            y += dy1
        } else {
            x += dx2
            y += dy2
        }
    }
}