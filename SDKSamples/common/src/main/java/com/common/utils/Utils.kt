package com.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller
import com.nanorep.nanoengine.model.configuration.DatestampFormatFactory
import com.nanorep.sdkcore.utils.toast
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

const val ERROR_DIALOG_REQUEST_CODE = 665

fun Activity.updateSecurityProvider() {

    if (Build.VERSION.SDK_INT < 21) {
        ProviderInstaller.installIfNeededAsync(
            this,
            object : ProviderInstaller.ProviderInstallListener {
                override fun onProviderInstallFailed(
                    errorCode: Int,
                    recoveryIntent: Intent?
                ) {
                    Log.e(
                        "Security-Installer",
                        "!!! failed to install security provider updates, Checking for recoverable error..."
                    )

                    GoogleApiAvailability.getInstance().apply {
                        if (isUserResolvableError(errorCode) &&
                            // check if the intent can be activated to prevent ActivityNotFoundException and
                            // to be able to display that "Messaging won't be available"
                            recoveryIntent?.resolveActivity(this@updateSecurityProvider.packageManager) != null
                        ) {

                            // Recoverable error. Show a dialog prompting the user to
                            // install/update/enable Google Play services.
                            showErrorDialogFragment(
                                this@updateSecurityProvider,
                                errorCode,
                                ERROR_DIALOG_REQUEST_CODE
                            ) {
                                // onCancel: The user chose not to take the recovery action
                                onProviderInstallerNotAvailable()
                            }
                        } else {
                            onProviderInstallerNotAvailable()
                        }
                    }
                }

                private fun onProviderInstallerNotAvailable() {
                    val msg =
                        "Google play services can't be installed or updated thous Messaging may not be available"
                    toast(baseContext, msg)
                    Log.e("Security-Installer", ">> $msg")
                }

                override fun onProviderInstalled() {
                    Log.i(
                        "Security-Installer",
                        ">> security provider updates installed successfully"
                    )

                }
            })
    }
}

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