package com.sdk.samples.topics

import androidx.annotation.NonNull
import com.integration.bold.boldchat.core.FormData
import com.nanorep.convesationui.bold.ui.FormListener
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.FormProvider
import com.sdk.samples.R
import com.sdk.samples.topics.extra.FormDummy

const val FORM_DUMMY_FRAGMENT_TAG = "form_dummy_fragment"

class BoldCustomChatForm : BotChat(){

    inner class FormProviderSample: FormProvider {
        override fun presentForm(formData: FormData, @NonNull callback: FormListener) {

            // Demo implementation that presents present a dummy form :
            val fragment = FormDummy.create(formData, callback)

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.chat_container, fragment, FORM_DUMMY_FRAGMENT_TAG)
                .addToBackStack(FORM_DUMMY_FRAGMENT_TAG)
                .commitAllowingStateLoss()
        }
    }

    override fun getBuilder(): ChatController.Builder {

        val settings = createChatSettings()

        return ChatController.Builder(this)
            .conversationSettings(settings)
            .formProvider(FormProviderSample())
    }
}