package com.sdk.samples.topics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.common.topicsbase.BoundDataFragment
import com.common.topicsbase.History
import com.common.topicsbase.SampleFormViewModel
import com.common.utils.chatForm.defs.ChatType
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.SingleLiveData
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.CarouselElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.FeedbackElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.IncomingElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.OutgoingElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.QuickOptionsElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.UploadElement
import com.nanorep.sdkcore.utils.Event
import com.nanorep.sdkcore.utils.children
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.R
import com.sdk.samples.databinding.BoldAvailabilityBinding
import com.sdk.samples.databinding.InterceptionTopicBinding
import java.util.Date

open class ElementsInterceptionChat : BotChatHistory() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
                .add(
                        R.id.basic_chat_view,
                        InterceptionConfig(),
                        topicTitle
                )
                .addToBackStack(topicTitle)
                .commit()
    }


}


class InterceptViewModel : ViewModel() {
    var interceptions: List<Data> = listOf() // array of [type, scoped]

    var announcements: List<Data> = listOf() // array of [type, scoped]


    private val submitForm = SingleLiveData<Boolean>()
    fun observeSubmission(owner: LifecycleOwner, observer: Observer<Boolean?>?) {
        submitForm.removeObservers(owner) // only 1 observer allowed
        observer?.let { submitForm.observe(owner, it) }
    }

    fun onSubmitForm(results: Boolean) { // true-startchat false-cancel
        submitForm.value = results
    }
}


open class Data(val type: Int, var scoped: Boolean = false)

class ViewData(type: Int, val resource: Int, scoped: Boolean = false)
    : Data(type, scoped)


class InterceptionConfig : BoundDataFragment<InterceptionTopicBinding>() {

    private val interceptViewModel: InterceptViewModel by activityViewModels()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
            InterceptionTopicBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createDataViews(InterceptElements, binding.interceptGroup)
        createDataViews(AnnouncedElements, binding.announceGroup)

        binding.startChat.setOnClickListener {
            submitData()
        }

    }

    private fun submitData(){
        interceptViewModel.interceptions = binding.interceptGroup.children().mapNotNull {
            (it as? CheckBox)?.takeIf { it.isChecked }?.let { Data(it.tag as Int)}
        }

        binding.interceptGroup.children().filter{ it is SwitchCompat}.forEach { switch ->
            interceptViewModel.interceptions.find { it == switch.tag}?.let {
                it.scoped = switch.isSelected } // is live only
        }


        interceptViewModel.announcements = binding.interceptGroup.children().mapNotNull {
            (it as? CheckBox)?.takeIf { it.isChecked }?.let { Data(it.tag as Int)}
        }

        //TODO: observe submission on activity, add interception and announcer mechanism.

    }

    private fun createDataViews(dataList: ArrayList<ViewData>, container: ViewGroup) {
        context?.let {
            container.removeAllViews()

            dataList.forEach { data ->
                val child = CheckBox(it).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 20

                    id = data.type * 2
                    this.text = context.getString(data.resource)

                    tag = data.type
                }

                container.addView(child)

                if (data.scoped) {
                    val switch = SwitchCompat(it).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 8
                        id = data.type * 4
                        this.text = context.getString(R.string.live_only)

                        tag = child.id
                    }

                    container.addView(switch)
                }
            }
        }
    }


    companion object {
        val InterceptElements = arrayListOf(ViewData(OutgoingElement, R.string.outgoing_element, true),
                ViewData(IncomingElement, R.string.incoming_element, true),
                ViewData(CarouselElement, R.string.carousel_element),
                ViewData(FeedbackElement, R.string.feedback_element),
                ViewData(QuickOptionsElement, R.string.options_element),
                ViewData(UploadElement, R.string.upload_element))

        val AnnouncedElements = arrayListOf(ViewData(OutgoingElement, R.string.outgoing_element),
                ViewData(IncomingElement, R.string.incoming_element),
                ViewData(QuickOptionsElement, R.string.options_element),
                ViewData(UploadElement, R.string.upload_element))
    }

}