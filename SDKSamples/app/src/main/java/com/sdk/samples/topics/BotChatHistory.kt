package com.sdk.samples.topics

import android.os.Bundle
import com.nanorep.convesationui.utils.HistoryMigration.Companion.start
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.topics.extra.withId
import com.sdk.samples.topics.history.HistoryMigrationProvider

open class BotChatHistory : History() {

    protected val account: BotAccount by lazy {
        Accounts.defaultBotAccount
    }
        @JvmName("account") get

    override fun getAccount(): Account {
        return account.withId(this)
    }

    override fun onUploadFileRequest() {
        toast(this@BotChatHistory, "The file upload action is not available for this sample.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        start(HistoryMigrationProvider(this){
            runOnUiThread { super.startChat()}
        })
    }

    override fun startChat() {

    }
}