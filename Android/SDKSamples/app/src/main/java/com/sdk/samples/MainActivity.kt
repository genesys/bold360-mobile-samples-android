package com.sdk.samples

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.sample_topic.view.*

class SampleTopic(val intentAction: String, val title: String, val icon: Drawable? = null)

class MainActivity : AppCompatActivity() {

    private lateinit var topics: ArrayList<SampleTopic>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        topics = arrayListOf(
            SampleTopic(
                "com.sdk.sample.action.BOT_CHAT",
                "Chat with BOT",
                resources.getDrawable(R.drawable.bot_avatar)
            ), SampleTopic(
                "com.sdk.sample.action.BOLD_CHAT",
                "Chat with BOLD",
                resources.getDrawable(R.drawable.live_avatar)
            ))

        topics_recycler.layoutManager = LinearLayoutManager(this)
        topics_recycler.adapter = TopicsAdapter(topics) { topic ->
            startActivity(Intent(topic.intentAction).putExtra("title", topic.title))
            overridePendingTransition(R.anim.right_in, R.anim.left_out);

        }
        topics_recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        (topics_recycler.adapter as TopicsAdapter).updateTopics()
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
            topic.icon?.setBounds(0,0,30,30)
            setCompoundDrawablesWithIntrinsicBounds(topic.icon, null, null, null)
        }
        itemView.tag = topic.intentAction
        itemView.setOnClickListener { startTopic(topic) }
    }

}
