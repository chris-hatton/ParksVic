package org.chrishatton.parksvic.ui.presenter

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.chrishatton.crosswind.rx.*
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.crosswind.util.Nullable
import geojson.Feature
import geojson.FeatureCollection
import org.chrishatton.parksvic.data.adapter.SiteFeatureAdapter
import org.chrishatton.parksvic.data.model.Site
import org.chrishatton.parksvic.data.model.api.ParksWebservice
import org.chrishatton.parksvic.ui.contract.DetailLevel
import org.chrishatton.parksvic.ui.contract.SitesViewContract
import org.chrishatton.parksvic.ui.contract.ZoomLevel
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class SitesPresenter : Presenter<SitesViewContract>() {

    override lateinit var view: SitesViewContract

    private lateinit var parksWebService : ParksWebservice

    private val selectedSiteSubject : BehaviorSubject<Nullable<Site>> = BehaviorSubject.create<Nullable<Site>>()
    val selectedSite : Observable<Nullable<Site>> = selectedSiteSubject

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

        selectedSite.subscribeOnLogicThread().subscribe { (site) ->
            detailPresenter.site = site
        }
        .addTo(subscriptions)

        val selectedSite : Observable<Site> = this.selectedSite.flatMap { (site) ->
            if(site==null) {
                Observable.never()
            } else {
                Observable.just(site)
            }
        }

        val zoomLevel : Observable<ZoomLevel> = view.userSelectedDetailLevel.map { detailLevel ->
            when( detailLevel ) {
                DetailLevel.NONE,
                DetailLevel.MINIMAL -> ZoomLevel.FAR
                DetailLevel.MEDIUM  -> ZoomLevel.MEDIUM
                DetailLevel.FULL    -> ZoomLevel.NEAR
            }
        }.distinctUntilChanged()

        Observables.combineLatest( selectedSite, zoomLevel ) { site, detailLevel ->
            site to detailLevel
        }
        .subscribeOnLogicThread()
        .observeOnUiThread()
        .subscribe { (site, detailLevel) ->
            view.focusSite(site,detailLevel)
        }
        .addTo( subscriptions )
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
                        .subscribeOnNetworkThread()
                        .map { it.features }
            }
            LoadMode.LAZY -> {
                view.viewportBoundingBoxes
                    .subscribeOnLogicThread()
                    .flatMap { boundingBoxes ->
                        val featureCollectionObservables : List<Observable<FeatureCollection>> = boundingBoxes.map { boundingBox ->
                            parksWebService.getParks( boundingBox = boundingBox )
                                    .subscribeOnNetworkThread()
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
        .observeOnLogicThread()

        featuresObservable
            .map { features -> features.map { siteFeatureAdapter.convert( it ) } }
            .observeOnUiThread()
            .subscribeBy(
                    onNext  = view::setDisplayedSites,
                    onError = {}
            )
            .addTo(subscriptions)
    }

    override fun onResume() {}
    override fun onPause() {}
    override fun onDestroy() {}

    lateinit var detailPresenter: SiteDetailPresenter

    fun onSelectSite( site: Site? ) {
        assertNotMainThread()
        selectedSiteSubject.onNext(Nullable(site))
    }
}