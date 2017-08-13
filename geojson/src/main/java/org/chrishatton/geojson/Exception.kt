package org.chrishatton.geojson

/**
 * Created by Chris on 13/08/2017.
 */
sealed class Exception : kotlin.Exception() {
    data class IllegalFormat( val reason: String ) : Exception()

}