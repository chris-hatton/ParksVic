package org.chrishatton.geobrowser.rx

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import opengis.model.app.MapViewLayer
import org.chrishatton.geobrowser.ui.contract.LayerViewContract
import kotlin.properties.Delegates

class RxRecyclerAdapter<VH: RxRecyclerAdapter.ViewHolder<T>,T>(
        private val itemsStream: Observable<Iterable<T>>,
        private val createViewHolder : (ViewGroup?, Int)-> RxRecyclerAdapter.ViewHolder<out T>
) : RecyclerView.Adapter<VH>() {

    private val layerViewBindingRelay : Relay<Pair<VH, T>> = BehaviorRelay.create()
    val layerViewBindingStream : Observable<Pair<VH, T>> = layerViewBindingRelay.hide()

    private val disposable = CompositeDisposable()
    private var items : Iterable<T> by Delegates.observable(emptyList()) { _,_,_ ->
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        itemsStream
            .subscribeBy(
                onNext  = { items = it },
                onError = { println(it) }
            )
            .addTo(disposable)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        disposable.dispose()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    abstract class ViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
            = createViewHolder.invoke(parent,viewType) as VH

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item : T = items.elementAt(position)
        //holder.itemView = item
        val binding = holder to item
        layerViewBindingRelay.accept(binding)
    }
}