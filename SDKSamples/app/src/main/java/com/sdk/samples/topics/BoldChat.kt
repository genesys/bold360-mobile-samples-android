//package com.sdk.samples.topics
//
//import com.common.topicsbase.BasicChat
//import com.common.utils.forms.toLiveAccount
//import com.nanorep.nanoengine.Account
//
//open class BoldChat : BasicChat() {
//    override val account: Account?
//        get() = accountData.toLiveAccount()
////
////    protected val account: BoldAccount by lazy {
////        Accounts.defaultBoldAccount
////    }
////    @JvmName("account") get
////
////    override fun getAccount(): Account {
////        return account
////    }
////
//}