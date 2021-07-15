package com.bold360.genesyshandover

import com.common.topicsbase.History
import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.AccountListenerEvent
import com.nanorep.convesationui.structure.HandoverHandler
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.handlers.AccountSessionListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.sdkcore.utils.Completion

class MainActivity : History() {

    override var chatType = ChatType.Bot

    val genAccount = GenAccount().apply {
        firstName = "Android"
        lastName = "Test"
        email = "peter.parker@marvel.com"
    }

    val accountProvider = GenAccountProvider()

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.apply {
            chatHandoverHandler(GenHandover(this@MainActivity, genAccount))
            accountProvider(accountProvider)
        }
    }


    /*override fun onChatStateChanged(stateEvent: StateEvent) {
        super.onChatStateChanged(stateEvent)

        when(stateEvent.state){

        }
    }*/

}

class GenAccountProvider : AccountSessionListener {
    override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {
        TODO("Not yet implemented")
    }

    override fun update(account: AccountInfo) {
        TODO("Not yet implemented")
    }

}
