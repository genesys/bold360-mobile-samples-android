package com.bold360.genesyshandover

import com.common.topicsbase.History
import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.genesys.cloud.messenger.transport.Configuration
import com.google.gson.JsonPrimitive
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.AccountListenerEvent
import com.nanorep.convesationui.structure.HandoverHandler
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.handlers.AccountSessionListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.sdkcore.utils.Completion

class MainActivity : History() {

    override var chatType = ChatType.Bot

    val genAccount = GenAccount()

    private val accountProvider = GenAccountProvider()

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.apply {
            chatHandoverHandler(GenHandover(this@MainActivity))
            accountProvider(accountProvider)
        }
    }

    override val extraDataFields: (() -> List<FormFieldFactory.FormField>)?
        get() = {
            (super.extraDataFields?.invoke()?.toMutableList() ?: mutableListOf()).apply {

                add(FormFieldFactory.TextField( ChatType.Bot, "Messaging Chat details:"))
                // adding genesys chat data:

                add(FormFieldFactory.TextInputField( ChatType.Bot, "deploymentId", GenAccount.DefaultConfig.deploymentId,
                    "Deployment id", false ))
                add(FormFieldFactory.TextInputField( ChatType.Bot, "messagingBaseAddress", GenAccount.DefaultConfig.messagingBaseAddress,
                    "Messaging base address", false ))
                add(FormFieldFactory.TextInputField( ChatType.Bot, "apiBaseAddress", GenAccount.DefaultConfig.apiBaseAddress,
                    "API base address", false ))
                add(FormFieldFactory.TextInputField( ChatType.Bot, "userFirstName", "",
                    "User first name", false ))
                add(FormFieldFactory.TextInputField( ChatType.Bot, "userLastName", "",
                    "User last name", false ))
                add(FormFieldFactory.TextInputField( ChatType.Bot, "userEmail", "",
                    "User email", false ))
                /*add(FormFieldFactory.Option( ChatType.ChatSelection, DataKeys.ChatTypeKey, ChatType.ContinueLast ))
                        add(FormFieldFactory.RestoreSwitch(false))*/
            }

        }

    override fun prepareAccount(): Account? {

        sampleFormViewModel.sampleData.value?.account?.let{
            genAccount.apply {
                val deploymentId = (it["deploymentId"] as? JsonPrimitive)?.asString ?: configuration.deploymentId
                val messagingBaseAddress = (it["messagingBaseAddress"] as? JsonPrimitive)?.asString ?: configuration.messagingBaseAddress
                val apiBaseAddress = (it["apiBaseAddress"] as? JsonPrimitive)?.asString ?: configuration.apiBaseAddress
                configuration = Configuration(deploymentId, messagingBaseAddress, apiBaseAddress,
                    getString(R.string.token_store_key))

                firstName = (it["userFirstName"] as? JsonPrimitive)?.asString?: "Android"
                lastName = (it["userLastName"] as? JsonPrimitive)?.asString ?: "Test"
                email = (it["userEmail"] as? JsonPrimitive)?.asString ?: "peter.parker@marvel.com"
            }
        }

        return super.prepareAccount()
    }

    override fun onAccountDataReady() {
        // prevents removal of account form, enables backing to it from the sample form
    }

    inner class GenAccountProvider : AccountSessionListener {
        override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {
            callback.onComplete(GenAccount.toHandoverAccount(genAccount))
        }

        override fun update(account: AccountInfo) {

        }

        override fun onConfigUpdate(account: AccountInfo, updateKey: String, updatedValue: Any?) {
            super.onConfigUpdate(account, updateKey, updatedValue)

            //TODO check for session
        }
    }
}