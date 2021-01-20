package com.sdk.samples.topics

//
//class PrechatExtraData : BotChat() {
//
//    override var formsParams = FormsParams.PrechatExtraData
//
//    override val account: Account
//        get() = accountData.toBotAccount().withId(this).also {
//            (loginData.extraData)?.let { extraData ->
//                extraData[JsonSharedDataHandler.preChat_deptCode_key]?.let {  BOLD_DEPARTMENT = it.toString() }
//                extraData[JsonSharedDataHandler.preChat_fName_key]?.let {  DemoFirstName = it.toString() }
//                extraData[JsonSharedDataHandler.preChat_lName_key]?.let {  DemoLastName = it.toString() }
//            }
//        }
//
//    /*override fun getAccount_old(): Account {
//
//
//
//        return (super.getAccount_old() as BotAccount).withId(this)
//    }*/
//
//    override fun getChatBuilder(): ChatController.Builder? {
//        return super.getChatBuilder()?.accountProvider( Companion )
//    }
//
//    companion object : SimpleAccountProvider() {
//
//        var BOLD_DEPARTMENT = "2278985919139590636"
//        var DemoFirstName = "Bold"
//        var DemoLastName = "360"
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
