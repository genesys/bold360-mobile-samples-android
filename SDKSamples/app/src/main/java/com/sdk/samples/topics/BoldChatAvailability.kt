package com.sdk.samples.topics

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.model.conversation.SessionInfoKeys
import com.nanorep.sdkcore.utils.runMain
import com.sdk.samples.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class BoldChatAvailability : BoldChat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadAvailabilityCheck()
    }

    private fun loadAvailabilityCheck() {

        val viewModel = ViewModelProvider(this).
            get(CheckAvailabilityViewModel::class.java).apply {
            account = getAccount() as BoldAccount

            onResults = { results ->
                if (results.isAvailable) {

//                    val acAccount = getAccount()
                    results.departmentId.takeIf { it > 0 }?.let {
                        account.addExtraData(SessionInfoKeys.Department to results.departmentId)
                    }

                    account.skipPrechat()
                    createChat()
                }
            }
        }

        supportFragmentManager.beginTransaction().add(R.id.chat_view, BoldAvailability(), AvailabilityTag)
            .addToBackStack(AvailabilityTag)
            .commit()
    }

    override fun startChat() {
    }

    override fun onChatUIDetached() {
        Log.d(AvailabilityTag, "live chat ended, back to availability checks")
        if(supportFragmentManager.backStackEntryCount > 1) {
            GlobalScope.launch(Dispatchers.Default) {
                delay(2500)
                runMain { onBackPressed() }
            }
        }
    }

    companion object{
        const val AvailabilityTag = "AvailabilityTag"
    }
}

