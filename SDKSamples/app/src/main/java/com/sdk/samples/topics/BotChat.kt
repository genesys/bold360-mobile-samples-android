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

            // To beused for mocking:(currently only supports "jio" account [see "/Users/trozin/Sites"]
            // account.setBaseURL("http://10.228.220.33/");

//        account.setWelcomeMessage("6576576575");
//       account.setWelcomeMessage( Carousel_WM);/*welcomeIds.get(new Random().nextInt(4))*/
//       account.setWelcomeMessage( BotAccount.None);

            // to test userId setting: account.userId("myuser1234");
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