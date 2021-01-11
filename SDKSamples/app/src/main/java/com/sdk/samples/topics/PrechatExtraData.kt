package com.sdk.samples.topics

import com.common.chatComponents.customProviders.SimpleAccountProvider
import com.common.chatComponents.customProviders.withId
import com.common.utils.loginForms.BotSharedDataHandler.Companion.preChat_deptCode_key
import com.common.utils.loginForms.BotSharedDataHandler.Companion.preChat_fName_key
import com.common.utils.loginForms.BotSharedDataHandler.Companion.preChat_lName_key
import com.common.utils.loginForms.accountUtils.ExtraParams
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.nanoengine.model.conversation.SessionInfoKeys

class PrechatExtraData : BotChat() {

    override val extraFormsParams = mutableListOf(ExtraParams.PrechatExtraData)

    override fun getAccount(): Account {

        (chatProvider.accountData.extraData)?.let { extraData ->
            extraData[preChat_deptCode_key]?.let {  BOLD_DEPARTMENT = it.toString() }
            extraData[preChat_fName_key]?.let {  DemoFirstName = it.toString() }
            extraData[preChat_lName_key]?.let {  DemoLastName = it.toString() }
        }

        return (super.getAccount() as BotAccount).withId(this)
    }

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.accountProvider( Companion )
    }

    companion object : SimpleAccountProvider() {

        var BOLD_DEPARTMENT = "2278985919139590636"
        var DemoFirstName = "Bold"
        var DemoLastName = "360"

        override fun addAccount(account: AccountInfo) {
            (account as? BoldAccount)?.apply {
                addExtraData (
                    SessionInfoKeys.Department to BOLD_DEPARTMENT,
                    SessionInfoKeys.FirstName to DemoFirstName,
                    SessionInfoKeys.LastName to DemoLastName)
            }
            super.addAccount(account)
        }
    }
}
