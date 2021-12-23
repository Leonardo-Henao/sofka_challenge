package com.sofka.sofkachallenge.utils

import android.util.Log

object MLog {
    fun see(msj: String, tag: String? = null) = Log.i(tag ?: "MLog", msj)
}