package com.sdk.samples.topics

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Checkable
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import androidx.fragment.app.FragmentOnAttachListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.common.chatComponents.history.HistoryRepository
import com.common.topicsbase.BoundDataFragment
import com.common.utils.AccessibilityAnnouncer
import com.common.utils.ElementsInterceptor
import com.common.utils.InterceptData
import com.common.utils.live.UploadFileChooser
import com.common.utils.live.onUploads
import com.nanorep.convesationui.fragments.NRConversationFragment
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.CarouselElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.FeedbackElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.IncomingElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.OutgoingElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.QuickOptionsElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.UndefinedElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.UploadElement
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.children
import com.nanorep.sdkcore.utils.px
import com.sdk.samples.R
import com.sdk.samples.databinding.InterceptionRuleBinding
import com.sdk.samples.databinding.InterceptionTopic2Binding
import com.sdk.samples.databinding.InterceptionTopicBinding
import com.sdk.samples.topics.ElementsInterceptionChat.Companion.TAG

/**
 * This sample will provide you the basic idea of what needs to be implemented in order to be able
 * to listen to chat elements intercept method.
 *
 * By using this method you can reject elements from getting into the chat but also add some logic
 * that can be preformed and condition the next chat flow. e.g. announcing url links and other elements
 * with the accessibility service APIs.
 * [more on Events and Notifications](https://logmein-bold-mobile.github.io/bold360-mobile-docs-android/docs/chat-configuration/tracking-events/events-and-notifications/)
 *
 */
class ElementsInterceptionChat : BotChatHistory(), OnBackStackChangedListener {

    private val announcer = AccessibilityAnnouncer(this)
    private lateinit var interceptor: ElementsInterceptor

    //!- needs to be initiated before Activity's onResume method since it registers to permissions requests
    private val uploadFileChooser = UploadFileChooser(this, 1024 * 1024 * 25)

    private val interceptViewModel: InterceptViewModel
        get() {
            return ViewModelProvider(this).get(InterceptViewModel::class.java)
        }

    /**
     * Sample options:
     * - Reset current filled configuration
     * - Switch between the 2 sample layouts
     */
    private var sampleMenu: SubMenu? = null

    /**
     * Defines the current sample
     */
    private var switchSample: Int = 0
    private val createSample
        get() = when (switchSample) {
            1 -> InterceptionConfig()
            else -> InterceptionRules()
        }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menu?.let { menu -> // adding Sample options menu
            sampleMenu = menu.addSubMenu(0, 0, 0, getString(R.string.sample_option_menu)).also {
                it.add(0, SwitchMenuId, SwitchMenu, getString(R.string.switch_sample_menu))
                it.add(0, ResetMenuId, ResetMenu, getString(R.string.reset_sample_menu))
                it.item.isEnabled = false
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            SwitchMenuId -> {
                switchSample = 1 - switchSample
                startSample(createSample, true)
            }
            ResetMenuId -> {
                interceptViewModel.onReset()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observe intercept form data ready state
        interceptViewModel.observeSubmission(this, { ready ->
            if (ready == true) {
                interceptor.apply {
                    interceptionRules = interceptViewModel.interceptions
                    announceRules = interceptViewModel.announcements
                }
                createChat()
            }
        })

        interceptor = ElementsInterceptor(this, announcer)

        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onUploadFileRequest() {
        uploadFileChooser.apply {
            onUploadsReady = chatController::onUploads
            open()
        }
    }

    override fun onAccountDataReady() {
        // prevents removal of account form, enables backing to it from the sample form
    }

    override fun startSample() {
        startSample(createSample)
    }

    private fun startSample(sample: InterceptionFrag<*>, switch: Boolean = false) {
        if (!supportFragmentManager.isStateSaved) { //!- check if we're not on saved state recovery to prevent state change and exceptions

            if (switch) { // replace sample layout without keeping switches in the backstack
                supportFragmentManager.popBackStack()
            }

            supportFragmentManager.beginTransaction()
                    .replace(R.id.basic_chat_view, sample, topicTitle)
                    .addToBackStack(topicTitle)
                    .commit()
        }
    }

    override fun getChatBuilder(): ChatController.Builder? {
        historyProvider = HistoryRepository(interceptor)
        updateHistoryRepo(account?.getGroupId())

        return super.getChatBuilder()
    }

    /**
     * This is the place to activate some logic related to the user selected url link
     * !- Links activation is NOT done by the SDK.
     */
    override fun onUrlLinkSelected(url: String) {
        // -> announcing link activation on chat
        announcer.announce("u r l  clicked  $url")
    }

    override fun onError(error: NRError) {
        super.onError(error)

        if(error.isConversationError()){
            interceptViewModel.onSubmitForm(false)
        }
    }

    override fun onStop() {
        if (isFinishing) {
            interceptViewModel.unobserveSubmission(this)
        }
        super.onStop()
    }

    override fun onBackStackChanged() {
        Log.v(TAG, "backstack changed, counts: ${supportFragmentManager.backStackEntryCount} fragments")
        when (supportFragmentManager.backStackEntryCount) {
            ActiveChat -> { // once the chat fragment is on:
                // notify submit change to the listening fragment
                interceptViewModel.onSubmitForm(false)
            }

            InterceptionForm -> { // Once back on the interception config forms, enable form options:
                enableMenu(sampleMenu?.item, true)
                return
            }
        }

        enableMenu(sampleMenu?.item, false)
    }

    companion object {

        const val TAG = "InterceptSample"

        private const val SwitchMenuId = 44
        private const val SwitchMenu = 0
        private const val ResetMenuId = 45
        private const val ResetMenu = 1

        private const val ActiveChat = 3
        private const val InterceptionForm = 2
    }
}


//<editor-fold desc=////////////// Data classes //////////////>

class InterceptViewModel : ViewModel() {
    var interceptions: List<InterceptData> = listOf()

    var announcements: List<InterceptData> = listOf()

    private val submitForm = MutableLiveData<Boolean>()
    fun observeSubmission(owner: LifecycleOwner, observer: Observer<Boolean>?): Observer<Boolean>? {
        return observer?.let {
            submitForm.observe(owner, it)
            it
        }
    }

    fun unobserveSubmission(owner: LifecycleOwner, observer: Observer<Boolean>? = null) {
        observer?.let {
            submitForm.removeObserver(it)
        } ?: submitForm.removeObservers(owner)
    }

    fun onSubmitForm(dataReady: Boolean) { // true-to startchat false-cancel
        submitForm.value = dataReady
    }

    private val resetForm = MutableLiveData<Unit>()
    fun observeReset(owner: LifecycleOwner, observer: Observer<Unit>?) {
        observer?.let { resetForm.observe(owner, it) }
    }

    fun onReset() {
        resetForm.postValue(Unit)
    }

    fun unobserveReset(owner: LifecycleOwner, observer: Observer<Unit>? = null) {
        observer?.let {
            resetForm.removeObserver(it)
        } ?: resetForm.removeObservers(owner)
    }
}


class ViewData(type: Int, val resource: Int, isLive: Boolean = false)
    : InterceptData(type, isLive)

//</editor-fold>


//<editor-fold desc=////////////// Interception configuration forms //////////////>

abstract class InterceptionFrag<T : ViewDataBinding> : BoundDataFragment<T>() {
    protected val interceptViewModel: InterceptViewModel by activityViewModels()

    protected var observeSubmission: Observer<Boolean>? = null
    protected var observeReset: Observer<Unit> = Observer {
            reset()
    }


    protected val startClickListener = View.OnClickListener { btn ->
        Log.v(TAG, "startChat clicked, clickable false")

        btn.isClickable = false // prevents multiple activation
        submitData()
   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            interceptViewModel.observeReset(it, observeReset)

            observeSubmission = interceptViewModel.observeSubmission(it) { state ->
                if(!state) enableSubmit(true)
            }
        }
    }

    abstract fun enableSubmit(enable: Boolean)

    abstract fun submitData()

    open fun reset(){
        enableSubmit(true)
    }

    override fun onStop() {
        activity?.let { activity ->
            observeSubmission?.let {
                interceptViewModel.unobserveSubmission(activity, it)
            }
                interceptViewModel.unobserveReset(activity, observeReset)

        }
        super.onStop()
    }
}

//<editor-fold desc=////////////// Interception configuration form 1 //////////////>

/**
 * Checkbox-es layout sample.
 * Interception and announcement of elements is configured according to checked types.
 *
 * Interception of elements is not limited to type or scope, you can check the `ElementModel`
 * provided on the intercept method and create your own logic.
 *
 */
class InterceptionConfig : InterceptionFrag<InterceptionTopicBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
            InterceptionTopicBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createDataViews(InterceptElements, binding.interceptGroup, 1)
        createDataViews(AnnouncedElements, binding.announceGroup, 2)

        binding.startChatConfig.setOnClickListener(startClickListener)

    }

    override fun enableSubmit(enable:Boolean) {
        binding.startChatConfig.isClickable = enable
    }

    override fun submitData() {
        val onSwitches = arrayListOf<Int>()
        interceptViewModel.interceptions = binding.interceptGroup.children()
                .filter { it is Checkable && it.isChecked }.mapNotNull {
                    when (it) {
                        is CheckBox -> InterceptData(it.tag as Int)
                        is SwitchCompat -> {
                            onSwitches.add(it.tag as Int)
                            null
                        }
                        else -> null
                    }
                }

        // go over switched on switches and set scoped if the matching CheckBoxes were checked:
        onSwitches.forEach { type ->
            interceptViewModel.interceptions.find { it.type == type }?.liveScope = true
        }

        interceptViewModel.announcements = binding.announceGroup.children().mapNotNull { view ->
            (view as? CheckBox)?.takeIf { it.isChecked }?.let { InterceptData(it.tag as Int) }
        }

        // notifies data is ready
        interceptViewModel.onSubmitForm(true)
    }

    private fun createDataViews(dataList: ArrayList<ViewData>, container: ViewGroup, idDelta: Int) {
        context?.let {
            container.removeAllViews()

            dataList.forEach { data ->
                val child = CheckBox(it).apply {
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                        topMargin = 10.px
                    }

                    id = data.type * idDelta
                    this.text = context.getString(data.resource)

                    tag = data.type
                }

                container.addView(child)

                if (data.liveScope) {
                    val switch = SwitchCompat(it).apply {
                        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            setMargins(22.px, (-6).px, 0, 0)
                        }
                        switchPadding = 6.px

                        id = data.type * (idDelta * 2)
                        this.text = context.getString(R.string.live_only)

                        tag = data.type
                    }

                    container.addView(switch)
                }
            }
        }
    }

    override fun reset() {
        super.reset()
        binding.interceptGroup.children().forEach {
            (it as? Checkable)?.isChecked = false
        }

        binding.announceGroup.children().forEach {
            (it as? Checkable)?.isChecked = false
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
                ViewData(CarouselElement, R.string.carousel_element),
                ViewData(QuickOptionsElement, R.string.options_element),
                ViewData(UploadElement, R.string.upload_element))
    }

}

//</editor-fold>


// <editor-fold desc=////////////// Interception configuration form 2 //////////////>

class InterceptionRule : InterceptData()
class AnnouncementRule : InterceptData()

/**
 * Rules list layout sample.
 * Interception and announcement of elements is configured by a set of rules added by the user.
 * Empty type rule will not be considered.
 *
 * Interception of elements is not limited to type or scope, you can check the `ElementModel`
 * provided on the intercept method and create your own logic.
 *
 */
class InterceptionRules : InterceptionFrag<InterceptionTopic2Binding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
            InterceptionTopic2Binding.inflate(inflater, container, false)

    private fun createRuleView(ruleTitle: String): View? {
        val ruleLayout = layoutInflater.inflate(R.layout.interception_rule, binding.rulesRoot, false)

        DataBindingUtil.bind<InterceptionRuleBinding>(ruleLayout)?.apply {
            ruleType.text = ruleTitle
            removeRule.setOnClickListener {
                binding.rulesRoot.removeView(ruleLayout)
            }
            elementType.apply {
                adapter = ArrayAdapter.createFromResource(requireContext(),
                        R.array.element_types, R.layout.spinner_item)
            }

            //-> extra customizations on the provided views can be done here...
        }

        return ruleLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.interceptionRule.setOnClickListener { btn ->
            createRuleView(getString(R.string.intercept_rule))?.let {
                it.tag = InterceptTag
                binding.rulesRoot.addView(it)
            }
        }

        binding.announcementRule.setOnClickListener { btn ->
            createRuleView(getString(R.string.announce_rule))?.let {
                it.tag = AnnounceTag
                binding.rulesRoot.addView(it)
            }
        }

        binding.startChatRules.setOnClickListener(startClickListener)

    }

    override fun reset() {
        super.reset()

        binding.rulesRoot.apply {
            removeAllViews()
            invalidate()
        }
    }

    override fun enableSubmit(enable:Boolean) {
        Log.d("intercept", "enableSubmit clickable ${enable}")

        binding.startChatRules.isClickable = enable
    }

    override fun submitData() {

        val rules = binding.rulesRoot.children().map { ruleView ->

            val bindRule: InterceptionRuleBinding? = DataBindingUtil.bind(ruleView)
            bindRule?.let {
                val rule = when (ruleView.tag) {
                    InterceptTag -> InterceptionRule()
                    else -> AnnouncementRule()
                }

                rule.type = (it.elementType.selectedItem as String).toType(requireContext())
                rule.liveScope = it.restrictLive.isChecked

                rule
            }
        }

        interceptViewModel.announcements = rules.filterIsInstance(AnnouncementRule::class.java)
        interceptViewModel.interceptions = rules.filterIsInstance(InterceptionRule::class.java)

        // notifies data is ready
        interceptViewModel.onSubmitForm(true)
    }

    private fun String.toType(context: Context): Int {
        val elementTypes = context.resources.getStringArray(R.array.element_types)
        val elementTypesIds = context.resources.getIntArray(R.array.element_types_id)

        return elementTypes.indexOf(this).takeUnless { it == -1 }?.let {
            elementTypesIds.get(it)
        } ?: UndefinedElement
    }

    companion object {
        private const val InterceptTag = "intercept"
        private const val AnnounceTag = "announce"
    }
}


//</editor-fold>
