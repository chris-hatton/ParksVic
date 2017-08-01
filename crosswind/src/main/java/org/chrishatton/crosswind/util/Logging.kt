package org.chrishatton.crosswind.util

import org.chrishatton.crosswind.environment

fun log( message: String ) = environment?.logger?.invoke(message)
