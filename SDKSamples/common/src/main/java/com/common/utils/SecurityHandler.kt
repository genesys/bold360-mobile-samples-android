package com.common.utils

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller
import com.nanorep.sdkcore.utils.NRError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlin.coroutines.resume


class SecurityInstaller {

    private var needUpdate : Boolean = true
    private var updateJob: Job? = null

    fun update(context:Activity, onError:((errorCode:String)->Unit)?){
        if(updateJob?.isActive == true) {
            return
        } // prevents relaunching if in progress

        // since this will occur most of the times only once we'll be using the GlobalScope
        updateJob = GlobalScope.takeIf { needUpdate }?.launch(Dispatchers.Main) {
            needUpdate = updateSecurityProvider(context)?.let { errorCode ->
                onError?.invoke(errorCode)
                true
            } != null

            Log.v(SECURITY_TAG, "Security needUpdate = $needUpdate")
        }
    }

    private suspend fun updateSecurityProvider(context: Activity) : String? {

        return when (Build.VERSION.SDK_INT >= 21) {
            true -> null

            else -> {
                suspendCancellableCoroutine { cont ->

                    ProviderInstaller.installIfNeededAsync(context,
                        object : ProviderInstaller.ProviderInstallListener {
                            override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent?) {
                                Log.e(SECURITY_TAG,
                                    "!!! failed to install security provider updates, Checking for recoverable error...")

                                GoogleApiAvailability.getInstance().apply {

                                    if (isUserResolvableError(errorCode) &&
                                        // check if the intent can be activated to prevent ActivityNotFoundException and
                                        // to be able to display that "Messaging won't be available"
                                        recoveryIntent?.resolveActivity(context.packageManager) != null) {

                                        // Recoverable error. Show a dialog prompting the user to
                                        // install/update/enable Google Play services.
                                        showErrorDialogFragment(context, errorCode, ERROR_DIALOG_REQUEST_CODE) {
                                            // onCancel: The user chose not to take the recovery action
                                            onProviderInstallerNotAvailable(true)
                                        }
                                    } else {
                                        onProviderInstallerNotAvailable(false)
                                    }
                                }
                            }

                            private fun onProviderInstallerNotAvailable(wasCanceled: Boolean) {
                                cont.resume(if (wasCanceled) NRError.Canceled else NRError.NotAvailable)
                            }

                            override fun onProviderInstalled() {
                                Log.i(SECURITY_TAG, ">> security provider updates installed successfully")
                                cont.resume(null)
                            }
                        }
                    )
                }
            }
        }
    }

    companion object {
        const val ERROR_DIALOG_REQUEST_CODE = 665
        const val SECURITY_TAG = "Security-Installer"
    }
}