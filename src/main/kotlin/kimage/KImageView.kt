package kimage

import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import kimage.model.Image
import kimage.model.Point
import kimage.model.pixel.RGB
import kotlin.properties.Delegates

typealias OverlayPlotter = (imageView:KImageView, gc: GraphicsContext, toDisplay: (Point)->Point2D )->Unit

class KImageView : Pane() {

    var overlayPlotter : OverlayPlotter? by Delegates.observable(null as OverlayPlotter?) { _,_,_->
        isNeedsLayout = true
    }

    var image : Image<RGB>? by Delegates.observable(null as Image<RGB>?) { _,_,image ->
        jfxColorImage = image?.map { rgb:RGB, _ -> JFXColorPixel(rgb) }
        isNeedsLayout = true
    }

    private var jfxColorImage : Image<JFXColorPixel>? = null

    private var canvas : Canvas = Canvas(width,height)

    init {
        children.add(canvas)
        canvas.widthProperty ().bind(widthProperty())
        canvas.heightProperty().bind(heightProperty())
    }



    override fun layoutChildren() {
        super.layoutChildren()

        val jfxColorImage = jfxColorImage

        val paneWidth  : Double = width
        val paneHeight : Double = height

        canvas.graphicsContext2D.clearRect(0.0,0.0,paneWidth,paneHeight)

        if(jfxColorImage == null) { return }

        val imageWidth  : Int = jfxColorImage.width
        val imageHeight : Int = jfxColorImage.height

        val paneAspect  : Double = paneWidth  / paneHeight  // Higher number means wider
        val imageAspect : Double = imageWidth.toDouble() / imageHeight.toDouble() // Higher number means wider

        val scaleFactor : Double
        val offsetX     : Int
        val offsetY     : Int

        val displayWidth  : Int
        val displayHeight : Int

        if( imageAspect > paneAspect ) { // When the image is wider than the pane...
            // ...scale width to fit, offset y.
            scaleFactor = imageWidth / paneWidth
            displayWidth = paneWidth.toInt()
            displayHeight = (imageHeight / scaleFactor).toInt()
            offsetX = 0
            offsetY = ((paneHeight - displayHeight) / 2.0).toInt()

        } else { // When the image is taller than the pane...
            // ...scale height to fit, offset x.
            scaleFactor = imageHeight / paneHeight
            displayWidth = (imageWidth / scaleFactor).toInt()
            displayHeight = paneHeight.toInt()
            offsetX = ((paneWidth - displayWidth) / 2.0).toInt()
            offsetY = 0
        }

        val gc = canvas.graphicsContext2D

        val pixelWriter = gc.pixelWriter
        for(y in 0 until displayHeight) {
            for(x in 0 until displayWidth) {
                val sourceX = (x * scaleFactor).toInt().coerceIn( 0 until jfxColorImage.width  )
                val sourceY = (y * scaleFactor).toInt().coerceIn( 0 until jfxColorImage.height )
                val color : Color = jfxColorImage[sourceX,sourceY].color
                pixelWriter.setColor(offsetX + x,offsetY + y,color)
            }
        }

        overlayPlotter?.let { plotOverlay ->
            plotOverlay(this,gc) { (x, y) ->
                Point2D((x / scaleFactor) + offsetX, (y / scaleFactor) + offsetY )
            }
        }
    }
}