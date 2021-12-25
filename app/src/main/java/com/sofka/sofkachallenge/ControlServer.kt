package com.sofka.sofkachallenge

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class ControlServer(context: Context) {

    private var rq: RequestQueue? = null

    init {
        rq = Volley.newRequestQueue(context)
    }

    fun addRq(stringRequest: StringRequest) {
        rq?.add(stringRequest)
        stringRequest.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 4f )
    }
}