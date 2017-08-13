package org.chrishatton.geojson

/**
 * https://tools.ietf.org/html/rfc7946#section-3.1.1
 */
data class Position(val longitude: Double, val latitude: Double, val altitude: Double? = null )