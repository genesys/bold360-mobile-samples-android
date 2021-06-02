package com.common.utils

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller
import com.nanorep.sdkcore.utils.toast

object SecurityHandler {
    const val ERROR_DIALOG_REQUEST_CODE = 665
    const val SECURITY_TAG = "Security-Installer"

    fun updateSecurityProvider(context: Activity, onSecurityResults:((upToDate:Boolean)->Unit)?) {

        if (Build.VERSION.SDK_INT < 21) {
            ProviderInstaller.installIfNeededAsync(
                context,
                object : ProviderInstaller.ProviderInstallListener {
                    override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent?) {
                        Log.e(
                            SECURITY_TAG,
                            "!!! failed to install security provider updates, Checking for recoverable error..."
                        )

                        GoogleApiAvailability.getInstance().apply {
                            if (isUserResolvableError(errorCode) &&
                                // check if the intent can be activated to prevent ActivityNotFoundException and
                                // to be able to display that "Messaging won't be available"
                                recoveryIntent?.resolveActivity(context.packageManager) != null
                            ) {

                                // Recoverable error. Show a dialog prompting the user to
                                // install/update/enable Google Play services.
                                showErrorDialogFragment(
                                    context,
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
                            "Google play services can't be installed or updated thous Messaging chat may not be available"
                        toast(context, msg)
                        Log.e(SECURITY_TAG, ">> $msg")

                        onSecurityResults?.invoke(false)
                    }

                    override fun onProviderInstalled() {
                        Log.i(SECURITY_TAG, ">> security provider updates installed successfully")
                        onSecurityResults?.invoke(true)
                    }
                })
        } else {
            onSecurityResults?.invoke(true)
        }
    }
}