package com.sdk.samples.topics

import android.os.Bundle
import android.util.Log
import com.common.topicsbase.History
import com.common.utils.customProviders.withId
import com.common.utils.history.HistoryMigrationProvider
import com.nanorep.convesationui.utils.HistoryMigration.Companion.start
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.toast
import kotlinx.coroutines.ExperimentalCoroutinesApi

class BotChatHistory : History() {

    override fun getAccount(): Account {
        return (super.getAccount() as BotAccount).withId(this)
    }

    override fun onUploadFileRequest() {
        toast(this@BotChatHistory, "The file upload action is not available for this sample.")
    }

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        start(HistoryMigrationProvider(this){
            runOnUiThread {
                Log.d("BotChatHistory", "Migration completed. starting chat...")
                super.startChat()
            }
        })
    }

    override fun startChat() {}
}