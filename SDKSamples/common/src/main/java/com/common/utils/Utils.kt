package com.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.common.topicsbase.SampleActivity
import com.nanorep.nanoengine.model.configuration.DatestampFormatFactory
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.toast
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

internal fun Context.isOnline() : Boolean =

    (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).let { manager ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.activeNetwork
        } else {
            manager.activeNetworkInfo
        } != null
    }.also {
        if (!it) {
            toast(
                this,
                getString(R.string.no_connection),
                Toast.LENGTH_SHORT
            )
        }
    }


fun AppCompatActivity.parseSecurityError(errorCode:String) =
    when(errorCode){
        NRError.Canceled -> getString(R.string.user_canceled_security_update)
        NRError.NotAvailable -> getString(R.string.security_update_not_available)
        else -> errorCode
    }


@JvmOverloads
fun SampleActivity<*>.toast(text: String, timeout: Int = Toast.LENGTH_LONG, background: Drawable? = null) {
    if(!isFinishing) {
        toast(this, text, timeout, background)
    }
}
