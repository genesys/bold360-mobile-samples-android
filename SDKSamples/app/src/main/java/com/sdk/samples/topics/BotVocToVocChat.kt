package com.sdk.samples.topics

import com.common.chatComponents.customProviders.CustomTTSAlterProvider
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.nanoengine.model.configuration.VoiceSettings
import com.nanorep.nanoengine.model.configuration.VoiceSupport

open class BotVocToVocChat : BotChat() {

    private val alterProvider = CustomTTSAlterProvider()

    override fun createChatSettings(): ConversationSettings {
        return super.createChatSettings().voiceSettings(VoiceSettings(VoiceSupport.HandsFree))
    }

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()
            ?.ttsReadAlterProvider( alterProvider ) // -> Comment this line to disable the read alter provider

    }
}