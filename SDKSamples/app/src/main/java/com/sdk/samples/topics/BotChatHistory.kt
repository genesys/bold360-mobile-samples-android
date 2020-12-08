package com.sdk.samples.topics

import android.os.Bundle
import android.util.Log
import com.nanorep.convesationui.utils.HistoryMigration.Companion.start
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.common.toAccount
import com.sdk.samples.topics.extra.withId
import com.sdk.samples.topics.history.HistoryMigrationProvider

open class BotChatHistory : History() {

    override fun getAccount(): Account {
        return  ((intent.getSerializableExtra("account"))?.toAccount() as? BotAccount
            ?: Accounts.defaultBotAccount).withId(this)
    }

    override fun onUploadFileRequest() {
        toast(this@BotChatHistory, "The file upload action is not available for this sample.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        start(HistoryMigrationProvider(this){
            runOnUiThread {
                Log.d("BotChatHistory", "Migration completed. starting chat...")
                super.startChat()
            }
        })
    }

    override fun startChat() {

    }
}