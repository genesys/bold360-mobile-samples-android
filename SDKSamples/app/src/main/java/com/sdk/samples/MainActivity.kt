package com.sdk.samples

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.topicsbase.SamplesViewModel
import com.common.topicsbase.SingletonSamplesViewModelFactory
import com.common.utils.ERROR_DIALOG_REQUEST_CODE
import com.common.utils.loginForms.AccountFormController
import com.common.utils.loginForms.accountUtils.ChatType
import com.common.utils.loginForms.accountUtils.ExtraParams.*
import com.common.utils.updateSecurityProvider
import com.nanorep.sdkcore.utils.weakRef
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.sample_topic.view.*

open class SampleTopic(
    val intentAction: String,
    val title: String,
    val icon: Drawable? = null,
    @ChatType val chatType: String = ChatType.None,
    val extraParams: List<String> = listOf()
)

class MainActivity : AppCompatActivity() {

    private lateinit var topics: ArrayList<SampleTopic>
    private lateinit var singletonSamplesViewModelFactory: SingletonSamplesViewModelFactory

    private var retryProviderInstall = true
    private lateinit var accountFormController: AccountFormController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.sample_toolbar))

        accountFormController = AccountFormController(
            samplesContainer.id, supportFragmentManager.weakRef()
        )

        topics = arrayListOf(
            SampleTopic(
                "com.sdk.sample.action.BOT_CHAT",
                getString(R.string.chat_with_bot),
                ContextCompat.getDrawable(this, R.drawable.outline_android_black_24),
                ChatType.Bot
            ), SampleTopic(
                "com.sdk.sample.action.HISTORY",
                getString(R.string.chat_with_bot_history),
                ContextCompat.getDrawable(this, R.drawable.baseline_history_black_24),
                ChatType.Bot,
                listOf(UsingHistory)
            ), SampleTopic(
                "com.sdk.sample.action.WELCOME_BOT_CHAT",
                getString(R.string.bot_chat_with_welcome),
                ContextCompat.getDrawable(this, R.drawable.outline_message_black_24),
                ChatType.Bot,
                listOf(Welcome)
            ), SampleTopic(
                "com.sdk.sample.action.VOICE_TO_VOICE",
                getString(R.string.bot_chat_with_voc_to_voc),
                ContextCompat.getDrawable(this, R.drawable.outline_hearing_black_24),
                ChatType.Bot
            ), SampleTopic(
                "com.sdk.sample.action.CONTEXT_SUPPORT",
                getString(R.string.bot_context_support),
                ContextCompat.getDrawable(this, R.drawable.context_support_24dp),
                ChatType.Bot,
                listOf(UsingContext)
            ), SampleTopic(
                "com.sdk.sample.action.ENTITIES",
                getString(R.string.bot_chat_with_entities),
                ContextCompat.getDrawable(this, R.drawable.entities_24)
            ),SampleTopic(
                "com.sdk.sample.action.HANDOVER",
                getString(R.string.bot_chat_with_handover),
                ContextCompat.getDrawable(this, R.drawable.baseline_pan_tool_black_24),
                ChatType.Bot
            ), SampleTopic(
                "com.sdk.sample.action.CUSTOM_UI",
                getString(R.string.custom_UI),
                ContextCompat.getDrawable(this, R.drawable.outline_rate_review_black_24),
                ChatType.Bot
            ), SampleTopic(
                "com.sdk.sample.action.AUTOCOMPLETE",
                getString(R.string.standalone_autocomplete),
                ContextCompat.getDrawable(this, R.drawable.outline_text_format_black_24),
                ChatType.Bot
            ), SampleTopic(
                "com.sdk.sample.action.BOLD_ASYNC_CHAT",
                getString(R.string.async_chat_with_an_agent),
                ContextCompat.getDrawable(this, R.drawable.outline_transform_black_24),
                ChatType.Async
            ), SampleTopic(
                "com.sdk.sample.action.ASYNC_CONTINUITY",
                getString(R.string.async_chat_continuity),
                ContextCompat.getDrawable(this, R.drawable.outline_transform_black_24),
                ChatType.Async,
                listOf(AsyncExtraData, UsingHistory)
            ), SampleTopic(
                "com.sdk.sample.action.PRE_CHAT_EXTRA_DATA",
                getString(R.string.bot_to_bold_with_prechat),
                ContextCompat.getDrawable(this, R.drawable.baseline_list_alt_black_24),
                ChatType.Bot,
                listOf(PrechatExtraData)
            ), SampleTopic(
                "com.sdk.sample.action.BOLD_CHAT_AVAILABILITY",
                getString(R.string.chat_with_bold),
                ContextCompat.getDrawable(this, R.drawable.outline_perm_identity_black_24),
                ChatType.Live
            ), SampleTopic(
                "com.sdk.sample.action.CUSTOM_FORM",
                getString(R.string.custom_form),
                ContextCompat.getDrawable(this, R.drawable.baseline_description_black_24),
                ChatType.Live
            ), SampleTopic(
                "com.sdk.sample.action.BOLD_CHAT_UPLOAD",
                "Custom upload on live chat",
                ContextCompat.getDrawable(this, R.drawable.outline_publish_black_24),
                ChatType.Live
            ), SampleTopic(
                "com.sdk.sample.action.BOLD_CHAT_UPLOAD_NO_UI",
                getString(R.string.bold_upload_without_ui),
                ContextCompat.getDrawable(this, R.drawable.outline_publish_black_24),
                ChatType.Live
            ), SampleTopic(
                "com.sdk.sample.action.RESTORE",
                getString(R.string.chat_restore),
                ContextCompat.getDrawable(this, R.drawable.baseline_restore_black_24),
                ChatType.None,
                listOf(UsingHistory)
            ), SampleTopic(
                "com.sdk.sample.action.FULL_DEMO_SAMPLE",
                getString(R.string.full_demo),
                ContextCompat.getDrawable(this, R.drawable.sample_image),
                ChatType.None,
                listOf(AsyncExtraData, UsingHistory, UsingContext, Welcome)
            )
        )
        singletonSamplesViewModelFactory =  SingletonSamplesViewModelFactory(
            SamplesViewModel.getInstance()
        )

        topics_recycler.layoutManager = LinearLayoutManager(this)
        topics_recycler.adapter = TopicsAdapter(topics) { topic ->

            accountFormController.updateChatType(topic.chatType, topic.extraParams) { account, restoreState, extraData ->

                ViewModelProvider(this, singletonSamplesViewModelFactory).get(SamplesViewModel::class.java).apply {
                    accountProvider.apply {
                        this.account = account
                        this.restoreState = restoreState
                        this.extraData = extraData
                    }
                }

                supportFragmentManager.fragments.forEach { _ ->
                    supportFragmentManager.popBackStackImmediate()
                }

                showLoading(true)

                startActivity(Intent(topic.intentAction).putExtra("title", topic.title))
                overridePendingTransition(R.anim.right_in, R.anim.left_out)

            }

        }

        topics_recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        (topics_recycler.adapter as TopicsAdapter).updateTopics()
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            sampleLoading.visibility = View.VISIBLE
            topics_recycler.visibility = View.GONE
        } else {
            sampleLoading.visibility = View.GONE
            topics_recycler.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        showLoading(false)
    }

    override fun onDestroy() {

        if (::singletonSamplesViewModelFactory.isInitialized) {
            singletonSamplesViewModelFactory.clear()
        }
        super.onDestroy()
    }

    override fun onPostResume() {
        super.onPostResume()
        if (retryProviderInstall) {
            this.updateSecurityProvider()
            retryProviderInstall = false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
            retryProviderInstall = true
        }
    }
}

class TopicsAdapter(var topics: ArrayList<SampleTopic>, val gotoTopic: (topic: SampleTopic) -> Unit) :
    RecyclerView.Adapter<TopicViewHolder>() {

    init {
        updateTopics()
    }

    fun updateTopics() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val itemView = inflate(parent.context, R.layout.sample_topic, null)
        return TopicViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(topics[position], gotoTopic)
    }
}


class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val titleView = itemView.title

    fun bind(topic: SampleTopic, startTopic: (intentAction: SampleTopic) -> Unit) {
        titleView.apply {
            text = topic.title
            topic.icon?.setBounds(0, 0, 30, 30)
            setCompoundDrawablesWithIntrinsicBounds(topic.icon, null, null, null)
        }
        itemView.tag = topic.intentAction
        itemView.setOnClickListener { startTopic(topic) }
    }

}
