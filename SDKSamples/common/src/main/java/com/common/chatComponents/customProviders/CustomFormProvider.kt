package com.common.chatComponents.customProviders

import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.common.topicsbase.SampleActivity
import com.integration.bold.boldchat.core.FormData
import com.integration.bold.boldchat.core.LanguageChangeRequest
import com.integration.bold.boldchat.visitor.api.Form
import com.integration.core.StateEvent
import com.integration.core.annotations.FormType
import com.nanorep.convesationui.bold.ui.ChatFormViewModel
import com.nanorep.convesationui.bold.ui.FormListener
import com.nanorep.convesationui.structure.controller.FormProvider
import com.nanorep.sdkcore.utils.weakRef

class CustomFormProvider(sampleActivity: SampleActivity<*>) : FormProvider {

    private val wActivity = sampleActivity.weakRef()

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

        /* form fragment arguments passed to it view ViewModel class.
           We're using the [com.nanorep.convesationui.bold.ui.ChatFormViewModel] provided by the SDK,
           since it fits our needs.
         */
        wActivity.get()?.run {

            ViewModelProvider(this).get(ChatFormViewModel::class.java).apply {
                //-> sets the form data (fields, branding) on the ViewModel for the CustomForm fragment to use
                onFormData(formData)

                //-> sets an observer to listen to form submission results.
                observeSubmission(this@run,
                    Observer { event ->
                        Log.e(CustomFormProvider_TAG, "Got form submission event ${event?.state}")

                        val isCanceled = event?.state == StateEvent.Canceled
                        if (isCanceled) {
                            formListener?.onCancel(formData.formType)

                        } else {
                            formListener?.onComplete(event?.data as? Form)
                        }
                    })

                observeLanguageChanges(this@run, Observer { languageChange ->
                    val language = languageChange?.first

                    Log.i("CustomForm", "Prechat: Language change detected: [${language.orEmpty()}]")

                    language?.run {
                        formListener?.onLanguageRequest(
                            LanguageChangeRequest(
                                this,
                                formData
                            )
                        ) { result ->
                            // update the forms `FormData` object, to trigger UI display update.
                            result.formData?.let {
                                this@apply.onFormData(it)
                            }

                            // on request callback passing language change approval results
                            languageChange.second.invoke(
                                result.error == null,
                                result.error?.toString()
                            )
                        }
                    }
                })
            }

            supportFragmentManager
                .beginTransaction()
                .add((this as SampleActivity<*>).containerId, BoldCustomForm.create(), CUSTOM_FORM_TAG)
                .addToBackStack(CUSTOM_FORM_TAG)
                .commit()
        }
    }

    companion object {
        const val CustomFormProvider_TAG = "CustomFormProvider"
        const val CUSTOM_FORM_TAG = "CustomForm"
    }
}