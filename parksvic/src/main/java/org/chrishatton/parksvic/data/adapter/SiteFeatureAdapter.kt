package org.chrishatton.parksvic.data.adapter

import org.chrishatton.geojson.Feature
import org.chrishatton.parksvic.data.model.Site
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import java.util.*

/**
 * Created by Chris on 30/07/2017.
 */
class SiteFeatureAdapter : FeatureAdapter<Site> {

    companion object {
        val dateTimeFormatter : DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()
    }

    override fun convert(feature: Feature): Site {

        fun <T> getField(key:String) : T? { return feature.properties[key] as? T }

        fun getDate(key:String) : Date? {
            val dateString : String = getField(key) ?: return null
            val dateTime : DateTime = dateTimeFormatter.parseDateTime(dateString)
            return dateTime.toDate()
        }

        // Fields ending 'C' seem to be comments

        val site = Site(
                location          = getField("the_geom"),
                facilityType      = getField("FAC_TYPE"), // 'REC SITES'
                versionDate       = getDate ("VERS_DATE"),
                serialNumber      = getField("SERIAL_NO"), // '3709' - actual ID better than feature ID?
                name              = getField("NAME"),
                latitude          = getField("LATITUDE"),
                longitude         = getField("LONGITUDE"),
                siteClass         = getField("SITE_CLASS"), // 'MID', 'BASIC', 'HIGH'
                wilderness        = getField("WILDERNESS"),
                disabledAccess    = getField("DIS_ACCESS"), // 'PARTIAL', 'NONE'
                accessDescription = getField("ACCESS_DSC"),
                fee               = getField("FEE"),
                comments          = getField("COMMENTS"),
                camping           = getField("CAMPING"), // 'CAMPING'
                campingC          = getField("CAMPING_C"),
                tb_visitor        = getField("TB_VISITOR"),
                tbvaC             = getField("TBVA_C"),
                heritage          = getField("HERITAGE"), // 'Y'
                heritageC         = getField("HERITAGE_C"),
                fishing           = getField("FISHING"),
                fishingC          = getField("FISHING_C"),
                fossicking        = getField("FOSSICKING"),
                fossickC          = getField("FOSSICK_C"),
                hang_glide        = getField("HANG_GLIDE"),
                hang_gldC         = getField("HANG_GLD_C"),
                paddling          = getField("PADDLING"),
                paddlingC         = getField("PADDLING_C"),
                picnicing         = getField("PICNICING"),
                picnicC           = getField("PICNIC_C"),
                pctAframe         = getField("PCT_AFRAME"),
                pctPedest         = getField("PCT_PEDEST"),
                pctOther          = getField("PCT_OTHER"),
                bbqElec           = getField("BBQ_ELEC"),
                bbqPit            = getField("BBQ_PIT"),
                bbqGas            = getField("BBQ_GAS"),
                bbqWood           = getField("BBQ_WOOD"),
                reduce_na         = getField("REDUCE_NA"),
                reduceC           = getField("REDUCE_C"),
                rockclimb         = getField("ROCKCLIMB"),
                rockclmbC         = getField("ROCKCLMB_C"),
                walkingdog        = getField("WALKINGDOG"),
                walkdogC          = getField("WALKDOG_C"),
                wildlife          = getField("WILDLIFE"),
                wildlifeC         = getField("WILDLIFE_C"),
                isPartOf          = getField("IS_PART_OF"), // '3694|3710' - Serial numbers?
                photoId1          = getField("PHOTO_ID_1"),
                photoId2          = getField("PHOTO_ID_2"),
                photoId3          = getField("PHOTO_ID_3"),
                label             = getField("LABEL"),
                actCode           = getField("ACT_CODE"), // 'E', 'WC'
                facCode           = getField("FAC_CODE"), // 'UY'
                published         = getField("PUBLISHED"),
                treeView          = getField("TREE_VIEW"),
                treeviewC         = getField("TREEVIEW_C"),
                closStat          = getField("CLOS_STAT"),
                closDesc          = getField("CLOS_DESC"),
                closStart         = getDate ("CLOS_START"),
                closOpen          = getDate ("CLOS_OPEN"),
                closReas          = getField("CLOS_REAS"),
                sdeDate           = getField("SDE_DATE"),  // '27/JUL/17'
                yCoord            = getField("Y_COORD"),
                xCoord            = getField("X_COORD")
        )

        return site
    }
}