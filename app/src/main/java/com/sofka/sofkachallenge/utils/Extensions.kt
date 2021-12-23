package com.sofka.sofkachallenge.utils

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import com.sofka.sofkachallenge.R

class Extensions {

    fun Context.showToast(msj: String) = Toast.makeText(this, msj, Toast.LENGTH_LONG).show()

    fun Context.createDialog(body_dialog : Int, cancelable : Boolean): Dialog {

        val dialog = Dialog(this, R.style.dialog_theme)
        dialog.setContentView(body_dialog)
        dialog.setCancelable(cancelable)

        return dialog
    }

}