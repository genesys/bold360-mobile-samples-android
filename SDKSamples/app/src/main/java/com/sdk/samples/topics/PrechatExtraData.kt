//package com.sdk.samples.topics
//
//import com.common.chatComponents.customProviders.SimpleAccountProvider
//import com.nanorep.convesationui.bold.model.BoldAccount
//import com.nanorep.nanoengine.AccountInfo
//import com.nanorep.nanoengine.model.conversation.SessionInfoKeys
//
//class PrechatExtraData : BotChat() {
//
///*
//    override fun getBuilder(): ChatController.Builder {
//        return super.getBuilder().accountProvider( Companion )
//    }
//*/
//
//    companion object : SimpleAccountProvider() {
//
//        const val BOLD_DEPARTMENT = "2278985919139590636"
//        const val DemoFirstName = "Bold"
//        const val DemoLastName = "360"
//
//        override fun addAccount(account: AccountInfo) {
//            (account as? BoldAccount)?.apply {
//                addExtraData (
//                    SessionInfoKeys.Department to BOLD_DEPARTMENT,
//                    SessionInfoKeys.FirstName to DemoFirstName,
//                    SessionInfoKeys.LastName to DemoLastName)
//            }
//            super.addAccount(account)
//        }
//    }
//}
