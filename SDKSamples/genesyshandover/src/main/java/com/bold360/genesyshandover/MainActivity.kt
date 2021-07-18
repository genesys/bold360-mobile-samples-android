package com.bold360.genesyshandover

import com.common.topicsbase.History
import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.genesys.cloud.messenger.io.Configuration
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
            chatHandoverHandler(GenHandover(this@MainActivity, genAccount))
            accountProvider(accountProvider)
        }
    }

    override val extraDataFields: (() -> List<FormFieldFactory.FormField>)?
        get() = {
            (super.extraDataFields?.invoke()?.toMutableList() ?: mutableListOf()).apply {
                add(FormFieldFactory.TextInputField( ChatType.Bot, "deploymentId", GenAccount.DefaultConfig.deploymentId,
                    "Deployment id", false ))
                add(FormFieldFactory.TextInputField( ChatType.Bot, "messagingBaseAddress", GenAccount.DefaultConfig.messagingBaseAddress,
                    "Messaging base address", false ))
                add(FormFieldFactory.TextInputField( ChatType.Bot, "apiBaseAddress", GenAccount.DefaultConfig.apiBaseAddress,
                    "API base address", false ))
            }

        }

    override fun prepareAccount(): Account? {

        sampleFormViewModel.sampleData.value?.account?.let{
            genAccount.apply {
                val deploymentId = it["deploymentId"]?.toString() ?: configuration.deploymentId
                val messagingBaseAddress = it["messagingBaseAddress"]?.toString() ?: configuration.messagingBaseAddress
                val apiBaseAddress = it["apiBaseAddress"]?.toString() ?: configuration.apiBaseAddress
                configuration = Configuration(deploymentId, messagingBaseAddress, apiBaseAddress)

                firstName = "Android"
                lastName = "Test"
                email = "peter.parker@marvel.com"
            }
        }

        return super.prepareAccount()
    }


    inner class GenAccountProvider : AccountSessionListener {
        override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {
            callback.onComplete(genAccount)
        }

        override fun update(account: AccountInfo) {
            //TODO check for session
        }

    }
}