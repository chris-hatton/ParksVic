package org.chrishatton.geobrowser.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import org.chrishatton.parksvic.data.model.Site

class SiteItem(val site: Site ) : ClusterItem {
    override fun getSnippet()  : String = site.comments ?: "Nothing is known about this camp site."
    override fun getTitle()    : String = site.name ?: "Camp site"
    override fun getPosition() : LatLng = LatLng( site.latitude!!, site.longitude!! )
}
