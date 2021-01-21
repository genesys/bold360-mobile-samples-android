//package com.sdk.samples.topics
//
//import com.nanorep.convesationui.bot.CarouselReadoutMessage
//import com.nanorep.convesationui.structure.components.ReadRequest
//import com.nanorep.convesationui.structure.components.TTSReadAlterProvider
//import com.nanorep.nanoengine.model.configuration.ConversationSettings
//import com.nanorep.nanoengine.model.configuration.VoiceSettings
//import com.nanorep.nanoengine.model.configuration.VoiceSupport
//
//open class BotVocToVocChat : BotChat() {
//
//    private val readAlterProvider:TTSReadAlterProvider = object : TTSReadAlterProvider{
//        override fun alter(readRequest: ReadRequest, callback: (ReadRequest) -> Unit) {
//            readRequest.readoutMessage.setPersistentPrefix("Persistent Option");
//            (readRequest.readoutMessage as? CarouselReadoutMessage)?.setPrefixToItemsOptions("Carousel Option");
//            readRequest.readoutMessage.setQuickPrefix("Quick Option");
//
//            // OR:
////            readRequest.readoutResult = "Response text was altered"
//
//            callback.invoke(readRequest)
//        }
//    }
//
//    override fun createChatSettings(): ConversationSettings {
//        return super.createChatSettings().voiceSettings(VoiceSettings(VoiceSupport.HandsFree))
//    }
///*
//    override fun getBuilder(): ChatController.Builder {
//        return super.getBuilder()
//           // Uncomment this to enable the read alter provider: .ttsReadAlterProvider(readAlterProvider)
//    }*/
//}