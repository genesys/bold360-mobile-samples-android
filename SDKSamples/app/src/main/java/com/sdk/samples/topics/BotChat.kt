package com.sdk.samples.topics

import android.content.Context
import android.content.SharedPreferences
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.toast

open class BotChat : BasicChat() {

    protected val account: BotAccount by lazy {
        Accounts.defaultBotAccount
    }
        @JvmName("account") get

    override fun getAccount(): Account {
        return account.apply {

            try {
                val preferences: SharedPreferences = this@BotChat.getSharedPreferences(
                    "bot_chat_session",
                    Context.MODE_PRIVATE
                )
                val userId = preferences.getString("botUserId_$account", null)
                this.userId = userId
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun onUploadFileRequest() {
        toast(this@BotChat, "The file upload action is not available for this sample.")
    }

}