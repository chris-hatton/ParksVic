package org.chrishatton.parksvic.ui.presenter

import org.chrishatton.crosswind.ui.presenter.Presenter
import org.chrishatton.parksvic.data.model.Site
import org.chrishatton.parksvic.ui.contract.SiteDetailViewContract
import kotlin.properties.Delegates

class SiteDetailPresenter : Presenter<SiteDetailViewContract>() {

    override lateinit var view: SiteDetailViewContract

    override fun onCreate() {}

    override fun onResume() {}

    override fun onPause() {}

    override fun onDestroy() {}

    var site : Site? by Delegates.observable<Site?>(null) { _,_,site ->

        if( site == null ) {

        } else {
            view.siteNameConsumer.accept( site.name      ?: "Unnamed site" )
            view.commentsConsumer.accept( site.comments  ?: "" )
            view.feeConsumer     .accept( site.fee       ?: "" )
            view.heritageConsumer.accept( site.heritageC ?: "" )
        }
    }
}