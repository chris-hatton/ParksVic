package org.chrishatton.parksvic.ui.presenter

import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.chrishatton.crosswind.rx.observeOnUiThread
import org.chrishatton.crosswind.rx.subscribeOnUiThread
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.crosswind.util.log
import org.chrishatton.parksvic.data.model.Site
import org.chrishatton.parksvic.data.model.api.ParksWebservice
import org.chrishatton.parksvic.ui.contract.SitesViewContract


class SitesPresenter : Presenter<SitesViewContract>() {

    override lateinit var view: SitesViewContract

    private lateinit var parksWebService : ParksWebservice

    fun onMapInitialized() {

        subscriptions += view.viewportBoundingBoxes
                .subscribeOnUiThread()
                .flatMap { boundingBoxes ->
                    val boxObservables = boundingBoxes.map { boundingBox ->
                        parksWebService.getParks( boundingBox = boundingBox )
                                .subscribeOn( Schedulers.io() )
                    }

                    Observable.zip(boxObservables) { results ->
                        results.fold( emptyArray<Site>() ) { accum,sites -> accum + (sites as Array<Site>) }
                    }
                }
                .observeOnUiThread()
                .subscribe( view.viewportSitesConsumer)
    }

    override fun onResume() {


    }

    override fun onCreate() {}
    override fun onPause() {}
    override fun onDestroy() {}
}