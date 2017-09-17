package kimage.model

data class Point(val x: Int, val y: Int) {

    infix operator fun plus( point: Point ) : Point {
        return Point( x + point.x, y + point.y )
    }

    infix operator fun minus( point: Point ) : Point {
        return Point( x - point.x, y - point.y )
    }
}
