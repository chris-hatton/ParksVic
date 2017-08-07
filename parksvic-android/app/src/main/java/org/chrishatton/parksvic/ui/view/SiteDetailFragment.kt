package org.chrishatton.parksvic.ui.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_parks.*
import org.chrishatton.crosswind.ui.view.PresentedFragment
import org.chrishatton.parksvic.R
import kotlinx.android.synthetic.main.fragment_site_detail.*
import org.chrishatton.parksvic.ui.contract.SiteDetailViewContract
import org.chrishatton.parksvic.ui.presenter.SiteDetailPresenter


class SiteDetailFragment : PresentedFragment<SiteDetailViewContract, SiteDetailPresenter>(), SiteDetailViewContract {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_site_detail, container, false)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun createPresenter( view: SiteDetailViewContract ): SiteDetailPresenter {
        return SiteDetailPresenter().apply { this.view = view }
    }

    private val siteSubscriptions : CompositeDisposable = CompositeDisposable()

    override val siteNameConsumer : Consumer<in CharSequence> by lazy { site_name_value_text.text() }
    override val feeConsumer      : Consumer<in CharSequence> by lazy { fee_value_text.text() }
    override val commentsConsumer : Consumer<in CharSequence> by lazy { comments_value_text.text() }
    override val heritageConsumer : Consumer<in CharSequence> by lazy { heritage_value_text.text() }

}
