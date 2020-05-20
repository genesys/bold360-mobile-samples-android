package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.nanoengine.model.configuration.VoiceSettings
import com.nanorep.nanoengine.model.configuration.VoiceSupport
import com.nanorep.sdkcore.utils.toast

open class BotVocToVocChat : BotChat() {

    override fun createChatSettings(): ConversationSettings {
        return super.createChatSettings().voiceSettings(VoiceSettings(VoiceSupport.VoiceToVoice))
    }
}