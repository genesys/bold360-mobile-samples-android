package com.sdk.samples.topics

import com.common.chatComponents.customProviders.SimpleAccountProvider
import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.AccountInfo


class PrechatExtraData : BotChat() {

    override val extraDataFields: (() -> List<FormFieldFactory.FormField>)
    get() = {
        listOf(
            FormFieldFactory.TextInputField(ChatType.Bot, DataKeys.FirstName, "", "First Name", false),
            FormFieldFactory.TextInputField(ChatType.Bot, DataKeys.LastName, "", "Last Name", false),
            FormFieldFactory.TextInputField(ChatType.Bot, DataKeys.Department, "", "Department code", false)
        )
    }

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.accountProvider( accountProvider )
    }

    private val accountProvider by lazy {

        object : SimpleAccountProvider() {

            val BOLD_DEPARTMENT = getDataByKey(DataKeys.Department) ?: "2278985919139590636"
            val DemoFirstName = getDataByKey(DataKeys.FirstName) ?: "Bold"
            val DemoLastName = getDataByKey(DataKeys.LastName) ?: "360"

            override fun addAccount(account: AccountInfo) {
                (account as? BoldAccount)?.apply {
                    addExtraData(
                        DataKeys.Department to BOLD_DEPARTMENT,
                        DataKeys.FirstName to DemoFirstName,
                        DataKeys.LastName to DemoLastName
                    )
                }
                super.addAccount(account)
            }
        }
    }
}
