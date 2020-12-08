package com.sdk.samples.topics

import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.nanoengine.model.conversation.SessionInfoKeys
import com.sdk.samples.common.BotSharedDataHandler.Companion.preChat_deptCode_key
import com.sdk.samples.common.BotSharedDataHandler.Companion.preChat_fName_key
import com.sdk.samples.common.BotSharedDataHandler.Companion.preChat_lName_key
import com.sdk.samples.common.toAccount
import com.sdk.samples.topics.extra.SimpleAccountProvider
import com.sdk.samples.topics.extra.withId

class PrechatExtraData : BotChat() {

    override fun getAccount(): Account {
        val intentAccountData = intent.getSerializableExtra("account")

        (intentAccountData as? Map<String,Any?>)?.let { accountDataMap ->
            (accountDataMap[preChat_deptCode_key] as? String)?.takeIf { it.isNotEmpty() }?.let { BOLD_DEPARTMENT = it }
            (accountDataMap[preChat_fName_key] as? String)?.takeIf { it.isNotEmpty() }?.let { DemoFirstName = it }
            (accountDataMap[preChat_lName_key] as? String)?.takeIf { it.isNotEmpty() }?.let { DemoLastName = it }
        }

        return (intentAccountData?.toAccount() as? BotAccount ?: Accounts.defaultBotAccount).withId(this)
    }

    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder().accountProvider( Companion )
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
