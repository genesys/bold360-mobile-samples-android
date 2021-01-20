package com.sdk.samples.topics

import android.os.Bundle
import android.util.Log
import com.common.chatComponents.customProviders.withId
import com.common.chatComponents.history.HistoryMigrationProvider
import com.common.utils.loginForms.dynamicFormPOC.toBotAccount
import com.nanorep.convesationui.utils.HistoryMigration
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.toast
import kotlinx.coroutines.ExperimentalCoroutinesApi

//class BotChatHistory : History() {
//
//    override val account: Account
//        get() = accountData.toBotAccount().withId(this)
//    override fun onUploadFileRequest() {
//        toast(this@BotChatHistory, "The file upload action is not available for this sample.")
//    }
//
//    @ExperimentalCoroutinesApi
//    override fun startChat(savedInstanceState: Bundle?) {
//        HistoryMigration.start(HistoryMigrationProvider(this) {
//            runOnUiThread {
//                Log.d("BotChatHistory", "Migration completed. starting chat...")
//                super.startChat(savedInstanceState)
//            }
//        })
//    }
//}