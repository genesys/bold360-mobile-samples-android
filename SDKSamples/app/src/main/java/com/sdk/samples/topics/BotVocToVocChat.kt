package com.sdk.samples.topics

import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.nanoengine.model.configuration.VoiceSettings
import com.nanorep.nanoengine.model.configuration.VoiceSupport

open class BotVocToVocChat : BotChat() {

    override fun createChatSettings(): ConversationSettings {
        return super.createChatSettings().voiceSettings(VoiceSettings(VoiceSupport.HandsFree))
    }

    // Uncomment this to enable the read alter provider
    /*override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.ttsReadAlterProvider(readAlterProvider)
    }*/
}