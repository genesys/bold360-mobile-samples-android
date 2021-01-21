//package com.sdk.samples.topics
//
//import android.os.Bundle
//import android.util.Log
//import com.common.chatComponents.customProviders.withId
//import com.common.chatComponents.history.HistoryMigrationProvider
//import com.common.topicsbase.History
//import com.common.utils.forms.Accounts
//import com.nanorep.convesationui.utils.HistoryMigration.Companion.start
//import com.nanorep.nanoengine.Account
//import com.nanorep.nanoengine.bot.BotAccount
//import com.nanorep.sdkcore.utils.toast
//
//open class BotChatHistory : History() {
//
//    protected val account: BotAccount by lazy {
//        Accounts.defaultBotAccount
//    }
//        @JvmName("account") get
//
//    override fun getAccount(): Account {
//        return account.withId(this)
//    }
//
//    override fun onUploadFileRequest() {
//        toast(this@BotChatHistory, "The file upload action is not available for this sample.")
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        start(HistoryMigrationProvider(this){
//            runOnUiThread {
//                Log.d("BotChatHistory", "Migration completed. starting chat...")
//                super.startChat()
//            }
//        })
//    }
//
//    override fun startChat() {
//
//    }
//}