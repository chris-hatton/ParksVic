package opengis.rx.ui

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kimage.model.Image
import kimage.model.pixel.RGB
import java.util.function.Consumer

/**
 * Created by Chris on 20/01/2018.
 */
/*
interface TileProvider<T:Tile> {
    val tileImages : Observable<Image<RGB>>
    val tilesConsumer : Consumer<T>
}

fun <T:Tile> opengis.process.TileProvider.toRx() = object : TileProvider<T> {

    private val tileImages = PublishSubject.create<Image<RGB>>()

    override val tileImages: Observable<Image<RGB>>
        get() = tileImages
    override val tilesConsumer: Consumer<T> = Consumer { tile ->
        this@toRx.getTile(tile) {

        }
    }
}
*/