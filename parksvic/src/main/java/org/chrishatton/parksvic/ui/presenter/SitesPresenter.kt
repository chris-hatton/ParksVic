package org.chrishatton.parksvic.ui.presenter

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.chrishatton.crosswind.rx.assertNotMainThread
import org.chrishatton.crosswind.rx.observeOnUiThread
import org.chrishatton.crosswind.rx.subscribeOnLogicThread
import org.chrishatton.crosswind.rx.subscribeOnUiThread
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.crosswind.util.log
import org.chrishatton.geojson.Feature
import org.chrishatton.geojson.FeatureCollection
import org.chrishatton.parksvic.data.adapter.SiteFeatureAdapter
import org.chrishatton.parksvic.data.model.Site
import org.chrishatton.parksvic.data.model.api.ParksWebservice
import org.chrishatton.parksvic.ui.contract.SitesViewContract
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class SitesPresenter : Presenter<SitesViewContract>() {

    override lateinit var view: SitesViewContract

    private lateinit var parksWebService : ParksWebservice

    override fun onCreate() {

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

        val BASE_URL : String = "http://10.0.1.68:8080/geoserver/"

        val retrofit = Retrofit.Builder()
                .baseUrl( BASE_URL )
                .client( client )
                .addConverterFactory( GsonConverterFactory.create() )
                .addCallAdapterFactory( RxJava2CallAdapterFactory.create() )
                .build()

        parksWebService = retrofit.create( ParksWebservice::class.java )
    }

    private enum class LoadMode {
        EAGER,
        LAZY
    }

    private val loadMode : LoadMode = LoadMode.EAGER

    fun onMapInitialized() {
        assertNotMainThread()

        val siteFeatureAdapter = SiteFeatureAdapter()

        val featuresObservable : Observable<List<Feature>> = when( loadMode ) {
            LoadMode.EAGER -> {
                parksWebService.getParks()
                    .subscribeOn( Schedulers.io() )
                    .map { it.features }
            }
            LoadMode.LAZY -> {
                view.viewportBoundingBoxes
                    .subscribeOnLogicThread()
                    .flatMap { boundingBoxes ->
                        val featureCollectionObservables : List<Observable<FeatureCollection>> = boundingBoxes.map { boundingBox ->
                            parksWebService.getParks( boundingBox = boundingBox )
                                    .subscribeOn( Schedulers.io() )
                        }

                        Observable.zip(featureCollectionObservables) { featureCollectionObjects ->
                            featureCollectionObjects.fold( emptyList<Feature>() ) { features,featureCollectionObject ->
                                val featureCollection : FeatureCollection = featureCollectionObject as FeatureCollection
                                features + featureCollection.features
                            }
                        }
                    }
            }
        }

        featuresObservable
            .map { features -> features.map { siteFeatureAdapter.convert( it ) } }
            .observeOnUiThread()
            .subscribeBy(
                    onNext  = view::onViewportSitesReceived,
                    onError = {}
            )
            .addTo(subscriptions)
    }

    override fun onResume() {}
    override fun onPause() {}
    override fun onDestroy() {}
}