package com.common.utils.chatForm

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.nanorep.sdkcore.utils.inflate
import com.nanorep.sdkcore.utils.px
import com.sdk.common.R
import com.sdk.common.databinding.ContextItemBinding
import kotlin.math.max

interface ContextAdapter {
    fun createContextView(
        botContext: Pair<String, String>? = null,
        onDelete: ((ContextViewHolder) -> Unit)? = null
    ): View?
}

///////////////////////////////////////////////////

interface ContextViewHolder {
    @Throws(AssertionError::class)
    fun getBotContext(): Pair<String, String>

    fun setBotContext(context: Pair<String, String>)
    fun getView(): View
}

///////////////////////////////////////////////////

class ContextBlock(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    LinearLayout(context, attrs, defStyle), ContextAdapter {

    lateinit var contextHandler: ContextHandler
    private lateinit var title: AppCompatTextView
    private lateinit var contextView: LinearContext

    init {

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        orientation = VERTICAL
    }

    lateinit var text: String

    fun initContextBlock(scroller: ScrollView?) {

        initTitle()

        initContextContainer(this, scroller)

        contextHandler = ContextHandler(contextView, this).apply {
            onDelete = { _ ->
                if (childCount == 2) {
                    title.visibility = View.GONE
                }
            }
        }
    }

    private fun initTitle() {

        title = AppCompatTextView(context).apply {
            text = context.getString(R.string.bot_context_title)
            textSize = 20f
            visibility = View.GONE
            setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                (this as MarginLayoutParams).setMargins(0, 16.px, 0, 0)
                gravity = Gravity.CENTER
            }
        }
        addView(title)
    }

    private fun initContextContainer(container: ViewGroup, scroller: ScrollView?) {

        contextView = (container.inflate(R.layout.context_view, container, false) as LinearContext).apply {
            this.scroller = scroller
            findViewById<Button>(R.id.add_context).setOnClickListener {
                try {
                    val lastContext = contextHandler.container.getLast()
                    if (lastContext == null || !lastContext.isEmpty()) {
                        if (lastContext == null) {
                            title.visibility = View.VISIBLE
                        }
                        contextHandler.addContext()
                    }
                } catch (ast: AssertionError) {
                    Log.w("ContextBlock", "got assertion error")
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

///////////////////////////////////////////////////

class ContextViewLinear @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ContextViewHolder {

    val binding = ContextItemBinding.inflate(LayoutInflater.from(context), this, true)

    init {

        if (layoutParams == null) {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = 5.px
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    marginStart = margin
                    marginEnd = margin
                }
                leftMargin = margin
                rightMargin = margin
                weightSum = 1f
                setPadding(margin, margin, margin, margin)
            }
        }
        orientation = HORIZONTAL
        binding.deleteContext.setOnClickListener {
            onDelete?.invoke(this)
        }
    }

    var onDelete: ((ContextViewHolder) -> Unit)? = null

    @Throws(AssertionError::class)
    override fun getBotContext(): Pair<String, String> {
        val key = binding.contextKey.text.toString()
        val value = binding.contextValue.text.toString()
        val blankKey = key.isBlank()
        val blankValue = value.isBlank()
        if (blankKey && !blankValue || blankValue && !blankKey) {
            throw AssertionError()
        }
        return key to value
    }

    override fun setBotContext(context: Pair<String, String>) {
        binding.contextKey.setText(context.first)
        binding.contextValue.setText(context.second)
    }

    override fun getView(): View {
        return this
    }
}

///////////////////////////////////////////////////

class ContextHandler(var container: ContextContainer, private val contextsAdapter: ContextAdapter) {

    var onDelete: ((ContextViewHolder) -> Unit)? = {
        container.removeContext(it)
    }
        set(value) {
            field = {
                container.removeContext(it)
                value?.invoke(it)
            }
        }

    fun addContext(botContext: Pair<String, String>? = null) {
        contextsAdapter.createContextView(botContext, onDelete)?.run {
            container.addContextView(this)
        }
    }

    @Throws(AssertionError::class)
    fun getContext(): Map<String, String>? {
        return container.getContextList()
    }
}

///////////////////////////////////////////////////

interface ContextContainer {
    fun addContextView(view: View)
    fun clear()
    @Throws(AssertionError::class)
    fun getContextList(): Map<String, String>?

    fun getLast(): Pair<String, String>?
    fun removeContext(contextView: ContextViewHolder)
}

///////////////////////////////////////////////////

class LinearContext @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ContextContainer {

    var scroller: ScrollView? = null

    override fun addContextView(view: View) {
        addView(view, max(childCount - 1, 0))
    }

    override fun removeContext(contextView: ContextViewHolder) {
        removeView(contextView.getView())
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed) {
            scroller?.scrollTo(l, b)
        }
    }

    override fun clear() {
        removeViews(0, childCount - 1)
    }

    override fun getContextList(): Map<String, String> {
        return (0 until childCount - 1).map { idx ->
            val entry = (getChildAt(idx) as? ContextViewHolder)?.getBotContext() ?: Pair("", "")
            entry.first to entry.second
        }.filterNot { it.first.isBlank() }.toMap() // remove empty pairs
    }

    override fun getLast(): Pair<String, String>? {
        return (takeIf { childCount > 1 }?.getChildAt(childCount - 2) as? ContextViewHolder)?.getBotContext()
    }
}