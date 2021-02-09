package com.sdk.samples.topics

import com.common.chatComponents.customProviders.SimpleAccountProvider
import com.common.utils.chat_form.FormFieldFactory
import com.common.utils.chat_form.defs.DataKeys
import com.common.utils.chat_form.defs.FormType
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.conversation.SessionInfoKeys


class PrechatExtraData : BotChat() {

    override var extraDataFields: () -> List<FormFieldFactory.FormField> = {
        listOf(
            FormFieldFactory.TextInputField(FormType.Account, DataKeys.preChat_fName, "", "First Name", false),
            FormFieldFactory.TextInputField(FormType.Account, DataKeys.LastName, "", "Last Name", false),
            FormFieldFactory.TextInputField(FormType.Account, DataKeys.preChat_deptCode, "", "Department code", false)
        )
    }

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.accountProvider( accountProvider )
    }

    private val accountProvider = object : SimpleAccountProvider() {

        val BOLD_DEPARTMENT = getDataByKey(DataKeys.preChat_deptCode) ?: "2278985919139590636"
        val DemoFirstName = getDataByKey(DataKeys.preChat_fName) ?: "Bold"
        val DemoLastName = getDataByKey(DataKeys.LastName) ?: "360"

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
