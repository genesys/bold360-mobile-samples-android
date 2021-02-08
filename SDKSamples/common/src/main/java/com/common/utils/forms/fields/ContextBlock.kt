package com.common.utils.forms.fields

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatTextView
import com.common.utils.forms.*
import com.nanorep.sdkcore.utils.inflate
import com.sdk.common.R
import kotlinx.android.synthetic.main.context_view.view.*

class ContextBlock(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle), ContextAdapter {

    lateinit var contextHandler: ContextHandler
    private lateinit var title: AppCompatTextView
    private lateinit var contextView: LinearContext

    init {
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun initContextBlock(container: ViewGroup, scroller: ScrollView?) {

        initTitle(container)

        initContextContainer(container, scroller)

        contextHandler = ContextHandler(contextView, this).apply {
            onDelete = { _ ->
                if (childCount == 0) { title.visibility = View.GONE }
            }
        }
    }

    private fun initTitle(container: ViewGroup) {

        title = AppCompatTextView(context).apply {
            text = "Bot Contexts:"
            textSize = 20f
            visibility = View.GONE
            setTextColor(Color.BLUE)
            setPadding(8, 14, 8, 14)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }
        container.addView(title)

    }

    private fun initContextContainer(container: ViewGroup, scroller: ScrollView?) {
        contextView = (container.inflate(R.layout.context_view, container, false) as LinearContext).apply {
            this.scroller = scroller
            add_context.setOnClickListener {
                try {
                    val lastContext = contextHandler.container.getLast()
                    if (lastContext == null || !lastContext.isEmpty()) {
                        if (lastContext == null) {
                            title.visibility = View.VISIBLE
                        }
                        contextHandler.addContext()
                    }
                } catch (ast: AssertionError) {
                    Log.w("AccountForm", "got assertion error")
                }
            }
            container.addView(this)
        }
    }

    override fun createContextView(
        botContext: Pair<String, String>?,
        onDelete: ((ContextViewHolder) -> Unit)?
    ): View {
        return ContextViewLinear(context).apply {
            this.onDelete = onDelete
            botContext?.run {
                this@apply.setBotContext(botContext)
            }
        }
    }
}