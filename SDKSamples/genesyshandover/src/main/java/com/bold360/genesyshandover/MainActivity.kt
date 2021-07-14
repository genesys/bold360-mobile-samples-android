package com.bold360.genesyshandover

import com.common.topicsbase.History
import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.HandoverHandler
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.model.configuration.ConversationSettings

class MainActivity : History() {

    override var chatType = ChatType.Bot

    val genAccount = GenAccount().apply {
        firstName = "Android"
        lastName = "Test"
        email = "peter.parker@marvel.com"
    }


    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.apply {
            this.chatHandoverHandler(GenHandover(this@MainActivity, genAccount))
        }
    }


    /*override fun onChatStateChanged(stateEvent: StateEvent) {
        super.onChatStateChanged(stateEvent)

        when(stateEvent.state){

        }
    }*/

}