package org.chrishatton.parksvic.ui.view

import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.chrishatton.parksvic.R
import org.chrishatton.parksvic.ui.contract.SitesViewContract
import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.parksvic.data.adapter.SiteFeatureAdapter
import org.chrishatton.parksvic.data.model.api.ParksWebservice
import org.chrishatton.parksvic.ui.presenter.SitesPresenter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class SitesActivity : PresentedActivity<SitesViewContract>(), OnMapReadyCallback {

    override val presenter: Presenter<SitesViewContract> = SitesPresenter()

    private var mapSubject: BehaviorSubject<GoogleMap> = BehaviorSubject.create<GoogleMap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parks)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

        val siteFeatureAdapter = SiteFeatureAdapter()

        Observables.combineLatest( mapSubject, parksWebService.getParks() ) { (map,parks) ->
            parks.map { featureCollection ->
            featureCollection.features.map { siteFeature ->
                siteFeatureAdapter.convert( siteFeature )
            }
        }

            .subscribeOn( Schedulers.io() )
            .observeOn( AndroidSchedulers.mainThread() )

        }
        .subscribeBy(
            onNext  = { sites ->
                Log.d("ParksActivity",sites.toString())
            },
            onError = { e ->
                e.printStackTrace()
            }
        )
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mapSubject.onNext(googleMap)

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //mapSubject!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mapSubject!!.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(LatLng(0,0),LatLng(0,0))))
//
//        mapSubject.value.setOnCameraIdleListener {
//            val bounds : LatLngBounds = mapSubject.value.projection.visibleRegion.latLngBounds
//            bounds.toBoundingBoxes().forEach( boundingBoxSubject::onNext )
//        }
    }
}
