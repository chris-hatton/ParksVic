package org.chrishatton.parksvic.ui.contract

import io.reactivex.functions.Consumer
import org.chrishatton.crosswind.ui.contract.ViewContract
import org.chrishatton.parksvic.data.model.Site

interface SiteDetailViewContract : ViewContract {

    val siteNameConsumer : Consumer<in CharSequence>
    val feeConsumer      : Consumer<in CharSequence>
    val commentsConsumer : Consumer<in CharSequence>
    val heritageConsumer : Consumer<in CharSequence>

}