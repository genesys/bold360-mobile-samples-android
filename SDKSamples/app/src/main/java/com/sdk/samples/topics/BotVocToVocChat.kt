package com.sdk.samples.topics

import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.nanoengine.model.configuration.VoiceSettings
import com.nanorep.nanoengine.model.configuration.VoiceSupport

class BotVocToVocChat : BotChat() {

    override fun createChatSettings(): ConversationSettings {
        return ConversationSettings().voiceSettings(VoiceSettings(VoiceSupport.HandsFree))
    }

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()
        // Uncomment this to enable the read alter provider: .ttsReadAlterProvider(CustomTTSAlterProvider())


    }
}