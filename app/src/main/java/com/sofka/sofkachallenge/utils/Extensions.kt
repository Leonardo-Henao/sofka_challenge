package com.sofka.sofkachallenge.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import com.sofka.sofkachallenge.R

fun Context.showToast(msj: String) = Toast.makeText(this, msj, Toast.LENGTH_LONG).show()

fun Context.createDialog(body_dialog: Int, cancelable: Boolean): Dialog {

    val dialog = Dialog(this, R.style.dialog_theme)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(LayoutInflater.from(this).inflate(body_dialog, null))
    dialog.setCancelable(cancelable)

    return dialog
}

@SuppressLint("InflateParams")
fun Context.startLoading(): Dialog {
    val dialog = Dialog(this, R.style.dialog_theme)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(LayoutInflater.from(this).inflate(R.layout.alert_dialog_loading, null))
    dialog.setCancelable(false)

    dialog.show()
    return dialog
}

fun Context.stoptLoading(dialog: Dialog) = dialog.dismiss()

@SuppressLint("InflateParams")
fun Context.startLoadingVerifyQuestion(): Dialog {
    val dialog = Dialog(this, R.style.dialog_theme)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(LayoutInflater.from(this).inflate(R.layout.alert_dialog_loading_verify_question, null))
    dialog.setCancelable(false)

    dialog.show()
    return dialog
}

fun Context.stoptLoadingVerifyQuestion(dialog: Dialog) = dialog.dismiss()
