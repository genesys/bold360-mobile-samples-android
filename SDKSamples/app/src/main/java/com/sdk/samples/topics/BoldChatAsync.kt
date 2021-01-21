//package com.sdk.samples.topics
//
//import com.common.topicsbase.History
//import com.common.utils.forms.Accounts
//import com.nanorep.convesationui.async.AsyncAccount
//import com.nanorep.nanoengine.Account
//
//open class BoldChatAsync : History() {
//
//    protected val account:AsyncAccount by lazy {
//        Accounts.defaultAsyncAccount
//    }
//
//    @JvmName("account") get
//    override fun getAccount(): Account {
//        return account
//    }
//
//}