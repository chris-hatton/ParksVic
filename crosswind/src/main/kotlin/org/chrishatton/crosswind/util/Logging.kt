package org.chrishatton.crosswind.util

import org.chrishatton.crosswind.Crosswind

fun log( message: String ) = Crosswind.environment.logger.invoke(message)
