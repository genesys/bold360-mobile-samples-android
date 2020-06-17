package com.sdk.samples.topics

import com.nanorep.convesationui.structure.components.ReadRequest
import com.nanorep.convesationui.structure.components.TTSReadAlterProvider
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.nanoengine.model.configuration.VoiceSettings
import com.nanorep.nanoengine.model.configuration.VoiceSupport

open class BotVocToVocChat : BotChat() {

    private val readAlterProvider:TTSReadAlterProvider = object : TTSReadAlterProvider{
        override fun alter(readRequest: ReadRequest, callback: (ReadRequest) -> Unit) {
//            readRequest.text = "Changing element text for testing: ${readRequest.readReqId}: original message: ${readRequest.text}"
            callback.invoke(readRequest)
        }
    }

    override fun createChatSettings(): ConversationSettings {
        return super.createChatSettings().voiceSettings(VoiceSettings(VoiceSupport.VoiceToVoice))
    }

    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder()
            .ttsReadAlterProvider(readAlterProvider) // Comment this to disable the read alter provider
    }
}