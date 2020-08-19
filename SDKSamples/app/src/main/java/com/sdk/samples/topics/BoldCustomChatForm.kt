package com.sdk.samples.topics

import androidx.annotation.NonNull
import com.integration.bold.boldchat.core.FormData
import com.nanorep.convesationui.bold.ui.FormListener
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.FormProvider
import com.sdk.samples.R
import com.sdk.samples.topics.extra.CustomForm

const val CUSTOM_LIVE_FORM = "custom_live_form"

class BoldCustomChatForm : BoldChat(){

    inner class FormProviderSample: FormProvider {
        override fun presentForm(formData: FormData, @NonNull callback: FormListener) {

            // Demo implementation that presents a custom form :
            val fragment = CustomForm.create(formData, callback)

            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out)
                .add(R.id.chat_view, fragment, CUSTOM_LIVE_FORM)
                .addToBackStack(CUSTOM_LIVE_FORM)
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