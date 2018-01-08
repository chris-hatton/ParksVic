package org.chrishatton.parksvic.data.model.api.cql

import org.chrishatton.parksvic.data.model.Circle

/**
 * Created by Chris on 29/07/2017.
 */
class DWithin( val circle: Circle) : CQLFilter {
    override fun toString(): String {
        return "DWITHIN(SHAPE,${circle.center},${circle.radius},meters)"
    }
}