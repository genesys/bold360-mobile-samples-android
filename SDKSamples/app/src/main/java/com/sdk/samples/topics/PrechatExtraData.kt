package com.sdk.samples.topics

import com.common.chatComponents.customProviders.SimpleAccountProvider
import com.common.utils.forms.FormFieldFactory
import com.common.utils.forms.defs.DataKeys
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.conversation.SessionInfoKeys


class PrechatExtraData : BotChat() {

    override var extraDataFields: () -> List<FormFieldFactory.FormField> = {
        listOf(
            FormFieldFactory.TextInputField(DataKeys.preChat_fName, "", "First Name", false),
            FormFieldFactory.TextInputField(DataKeys.LastName, "", "Last Name", false),
            FormFieldFactory.TextInputField(DataKeys.preChat_deptCode, "", "Department code", false)
        )
    }

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.accountProvider( accountProvider )
    }

    val accountProvider = object : SimpleAccountProvider() {

        var BOLD_DEPARTMENT = accountData[DataKeys.preChat_deptCode] ?: "2278985919139590636"
        var DemoFirstName = accountData[DataKeys.preChat_fName] ?: "Bold"
        var DemoLastName = accountData[DataKeys.LastName] ?: "360"

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
