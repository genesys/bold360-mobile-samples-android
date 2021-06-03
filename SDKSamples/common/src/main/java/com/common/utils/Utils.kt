package com.common.utils

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.nanorep.nanoengine.model.configuration.DatestampFormatFactory
import com.nanorep.sdkcore.utils.NRError
import com.sdk.common.R
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale

class SampleDatestampFactory : DatestampFormatFactory {

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("EEEEE MMM HH:mm:ss", DateFormatSymbols(Locale.getDefault()).apply {
        weekdays = arrayOf(
            "Unused",
            "Sad Sunday",
            "Manic Monday",
            "Thriving Tuesday",
            "Wet Wednesday",
            "Total Thursday",
            "Fat Friday",
            "Super Saturday")
    })

    override fun formatDate(datestamp: Long): String {
        return simpleDateFormat.format(datestamp)
    }
}

fun AppCompatActivity.parseSecurityError(errorCode:String) =
    when(errorCode){
        NRError.Canceled -> getString(R.string.user_canceled_security_update)
        NRError.NotAvailable -> getString(R.string.security_update_not_available)
        else -> errorCode
    }
