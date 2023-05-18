package com.sdk.samples

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.utils.SecurityInstaller
import com.common.utils.parseSecurityError
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.databinding.ActivityMainBinding
import com.sdk.samples.databinding.SampleTopicBinding
import java.util.ArrayList

open class SampleTopic(val intentAction: String, val title: String, val icon: Drawable? = null)

class MainActivity : AppCompatActivity() {

    private val securityInstaller = SecurityInstaller()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main)

        (binding.samplesToolbar as? Toolbar)?.let {
            setSupportActionBar(it)
        }

        arrayListOf(

            SampleTopic(
                "com.sdk.sample.action.BOT_CHAT",
                getString(R.string.chat_with_bot),
                ContextCompat.getDrawable(this, R.drawable.outline_android_black_24)

            ), SampleTopic(
                "com.sdk.sample.action.HISTORY",
                getString(R.string.chat_with_bot_history),
                ContextCompat.getDrawable(this, R.drawable.baseline_history_black_24)

            ), SampleTopic(
                "com.sdk.sample.action.WELCOME_BOT_CHAT",
                getString(R.string.bot_chat_with_welcome),
                ContextCompat.getDrawable(this, R.drawable.outline_message_black_24)

            ), SampleTopic(
                "com.sdk.sample.action.VOICE_TO_VOICE",
                getString(R.string.bot_chat_with_voc_to_voc),
                ContextCompat.getDrawable(this, R.drawable.outline_hearing_black_24)

            ), SampleTopic(
                "com.sdk.sample.action.CONTEXT_SUPPORT",
                getString(R.string.bot_context_support),
                ContextCompat.getDrawable(this, R.drawable.context_support_24dp)

            ), SampleTopic(
                "com.sdk.sample.action.ENTITIES",
                getString(R.string.bot_chat_with_entities),
                ContextCompat.getDrawable(this, R.drawable.entities_24)

            ), SampleTopic(
                "com.sdk.sample.action.INTERCEPTION",
                getString(R.string.bot_chat_with_interception),
                ContextCompat.getDrawable(this, R.drawable.intercept_24)

            ),SampleTopic(
                "com.sdk.sample.action.HANDOVER",
                getString(R.string.bot_chat_with_handover),
                ContextCompat.getDrawable(this, R.drawable.baseline_pan_tool_black_24)

            )/*, SampleTopic(
                "com.sdk.sample.action.BOLD_ASYNC_CHAT",
                getString(R.string.async_chat_with_an_agent),
                ContextCompat.getDrawable(this, R.drawable.outline_transform_black_24)

            ), SampleTopic(
                "com.sdk.sample.action.ASYNC_CONTINUITY",
                getString(R.string.async_chat_continuity),
                ContextCompat.getDrawable(this, R.drawable.outline_transform_black_24)

            )*/, SampleTopic(
                "com.sdk.sample.action.BOLD_CHAT_AVAILABILITY",
                getString(R.string.chat_with_bold),
                ContextCompat.getDrawable(this, R.drawable.outline_perm_identity_black_24)

            ), SampleTopic(
                "com.sdk.sample.action.CUSTOM_FORM",
                getString(R.string.custom_form),
                ContextCompat.getDrawable(this, R.drawable.baseline_description_black_24)

            ), SampleTopic(
                "com.sdk.sample.action.PRE_CHAT_EXTRA_DATA",
                getString(R.string.bot_to_bold_with_prechat),
                ContextCompat.getDrawable(this, R.drawable.baseline_list_alt_black_24)

            ),  SampleTopic(
                "com.sdk.sample.action.BOLD_CHAT_UPLOAD",
                "Custom upload on live chat",
                ContextCompat.getDrawable(this, R.drawable.outline_publish_black_24)

            ), SampleTopic(
                "com.sdk.sample.action.BOLD_CHAT_UPLOAD_NO_UI",
                getString(R.string.bold_upload_without_ui),
                ContextCompat.getDrawable(this, R.drawable.outline_publish_black_24)

            ), SampleTopic(
                "com.sdk.sample.action.RESTORE",
                getString(R.string.chat_restore),
                ContextCompat.getDrawable(this, R.drawable.baseline_restore_black_24)

            ), SampleTopic(
                "com.sdk.sample.action.CUSTOM_UI",
                getString(R.string.custom_UI),
                ContextCompat.getDrawable(this, R.drawable.outline_rate_review_black_24)

            ), SampleTopic(
                "com.sdk.sample.action.AUTOCOMPLETE",
                getString(R.string.standalone_autocomplete),
                ContextCompat.getDrawable(this, R.drawable.outline_text_format_black_24)
            )

        ).let { topics ->

            binding.topicsRecycler.layoutManager = LinearLayoutManager(this)

            binding.topicsRecycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

            binding.topicsRecycler.adapter = TopicsAdapter(topics) { topic ->
                startActivity(Intent(topic.intentAction).putExtra("title", topic.title))
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }.also {
                it.updateTopics() // to be moved to the
            }
        }
    }

    override fun onPostResume() {
        super.onPostResume()

        securityInstaller.update(this){ errorCode ->
            val msg = parseSecurityError(errorCode)
            toast(this, msg)
            Log.e(SecurityInstaller.SECURITY_TAG, ">> $msg")
        }
    }

 }

class TopicsAdapter(var topics: ArrayList<SampleTopic>, private val gotoTopic: (topic: SampleTopic) -> Unit) :
    RecyclerView.Adapter<TopicViewHolder>() {

    init {
        updateTopics()
    }

    fun updateTopics() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder(SampleTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(topics[position], gotoTopic)
    }
}

class TopicViewHolder(binding: SampleTopicBinding) : RecyclerView.ViewHolder(binding.root) {

    private val titleView = binding.title

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
