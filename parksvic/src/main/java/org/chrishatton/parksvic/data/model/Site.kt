package org.chrishatton.parksvic.data.model

import java.util.*

/**
 * Created by Chris on 29/07/2017.
 */
class Point


fun <T> getField( name: String ) : T { TODO() }
data class Site(
        val location          : Point?,
        val facilityType      : String?,
        val versionDate       : Date?,
        val serialNumber      : String?,
        val name              : String?,
        val latitude          : Double?,
        val longitude         : Double?,
        val siteClass         : String?,
        val wilderness        : String?,
        val disabledAccess    : String?,
        val accessDescription : String?,
        val fee               : String?,
        val comments          : String?,
        val camping           : String?,
        val campingC          : String?,
        val tb_visitor        : String?,
        val tbvaC             : String?,
        val heritage          : String?,
        val heritageC         : String?,
        val fishing           : String?,
        val fishingC          : String?,
        val fossicking        : String?,
        val fossickC          : String?,
        val hang_glide        : String?,
        val hang_gldC         : String?,
        val paddling          : String?,
        val paddlingC         : String?,
        val picnicing         : String?,
        val picnicC           : String?,
        val pctAframe         : String?,
        val pctPedest         : String?,
        val pctOther          : String?,
        val bbqElec           : String?,
        val bbqPit            : String?,
        val bbqGas            : String?,
        val bbqWood           : String?,
        val reduce_na         : String?,
        val reduceC           : String?,
        val rockclimb         : String?,
        val rockclmbC         : String?,
        val walkingdog        : String?,
        val walkdogC          : String?,
        val wildlife          : String?,
        val wildlifeC         : String?,
        val isPartOf          : String?,
        val photoId1          : String?,
        val photoId2          : String?,
        val photoId3          : String?,
        val label             : String?,
        val actCode           : String?,
        val facCode           : String?,
        val published         : String?,
        val treeView          : String?,
        val treeviewC         : String?,
        val closStat          : String?,
        val closDesc          : String?,
        val closStart         : Date?,
        val closOpen          : Date?,
        val closReas          : String?,
        val sdeDate           : String?,
        val yCoord            : Double?,
        val xCoord            : Double?
)