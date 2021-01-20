package com.sdk.samples.topics

//
//class ContextSupportChat : BotChat() {
//
//    override val account: Account
//        get() = accountData.toBotAccount().apply {
//            contexts = mapOf(
//                "ContextKey1" to "ContextValue1",
//                "ContextKey2" to "ContextValue2"
//            )
//        }
//
//    override var formsParams = FormsParams.UsingContext
//
//    /*override fun getAccount_old(): Account {
//        return (super.getAccount_old() as BotAccount).apply {
//            contexts = mapOf(
//                "ContextKey1" to "ContextValue1",
//                "ContextKey2" to "ContextValue2"
//            )
//        }
//    }*/
//}