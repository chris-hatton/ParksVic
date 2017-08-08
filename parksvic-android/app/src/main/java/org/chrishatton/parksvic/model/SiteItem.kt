package org.chrishatton.parksvic.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import org.chrishatton.parksvic.data.model.Site

class SiteItem(val site: Site ) : ClusterItem {
    override fun getSnippet(): String {
        return site.comments ?: "Nothing is known about this camp site."
    }

    override fun getTitle(): String {
        return site.name ?: "Camp site"
    }

    override fun getPosition(): LatLng {
        return LatLng( site.latitude!!, site.longitude!! )
    }
}