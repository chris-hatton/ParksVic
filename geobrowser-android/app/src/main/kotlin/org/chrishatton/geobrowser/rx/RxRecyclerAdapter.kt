package org.chrishatton.geobrowser.rx

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import kotlin.properties.Delegates

class RxRecyclerAdapter<VH: RxRecyclerAdapter.ViewHolder<T>,T>(
        private val itemsStream: Observable<Iterable<T>>,
        private val createViewHolder : (ViewGroup?, Int)-> VH
) : RecyclerView.Adapter<VH>() {

    private val disposable = CompositeDisposable()
    private var items : Iterable<T> by Delegates.observable(emptyList()) { _,_,_ ->
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        itemsStream
            .subscribeBy(
                onNext  = { items = it },
                onError = { items = emptyList() }
            )
            .addTo(disposable)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        disposable.dispose()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    abstract class ViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var item : T
            get() = itemSubject.value
            set(value) = itemSubject.onNext(value)

        private val itemSubject = BehaviorSubject.create<T>()
        val itemStream : Observable<T> = itemSubject.hide()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
            = createViewHolder(parent,viewType)

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.item = items.elementAt(position)
    }
}