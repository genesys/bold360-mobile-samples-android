package com.sdk.samples.topics

import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.integration.bold.boldchat.core.FormData
import com.integration.bold.boldchat.visitor.api.Form
import com.integration.core.StateEvent
import com.integration.core.annotations.FormType
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.bold.ui.FormListener
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.FormProvider
import com.sdk.samples.R
import com.sdk.samples.topics.extra.CustomForm
import com.sdk.samples.topics.extra.FormViewModel


class BoldCustomChatForm : BoldChatAvailability() {

    inner class FormProviderSample : FormProvider {

        // prevent the lose of inner references, when being used within the submitForm observer.
        private var formListener: FormListener? = null

        override fun presentForm(formData: FormData, @NonNull callback: FormListener) {

            //////////////////////////////////////
            //-> Pass null to in order to indicate the SDK to display its provided postchat from:
            //////////////////////////////////////
            if (formData.formType == FormType.PostChatForm) {
                callback.onComplete(null)
                return
            }

            //////////////////////////////////////
            // -> Other forms will be customized:
            //////////////////////////////////////

            this.formListener = callback

            ViewModelProvider(this@BoldCustomChatForm).get(FormViewModel::class.java).apply {

                // set the form data (fields, branding) on the ViewModel for the CustomForm fragment to use
                this.data = formData

                // sets an observer to listen to form submission results.
                observeSubmission(this@BoldCustomChatForm,
                    Observer { event ->
                        Log.e(CustomFormTag, "Got form submission event ${event?.state}")

                        val isCanceled = event?.state == StateEvent.Canceled
                        if (isCanceled) {
                            formListener?.onCancel(formData.formType)

                        } else {
                            formListener?.onComplete(event?.data as? Form)
                        }
                    })

            }

            // Demo implementation that presents a custom form :
            val fragment = CustomForm.create()

            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.right_in,
                    R.anim.right_out,
                    R.anim.right_in,
                    R.anim.right_out
                )
                .add(R.id.chat_view, fragment, CustomFormTag)
                .addToBackStack(CustomFormTag)
                .commit()
        }
    }

    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder().formProvider(FormProviderSample())
    }

    override fun prepareAccount(account: BoldAccount) {

    }

    companion object {
        private const val CustomFormTag = "CustomLiveForm"
    }
}