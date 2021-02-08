package com.common.utils.forms

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import com.nanorep.sdkcore.utils.px
import com.sdk.common.R
import kotlinx.android.synthetic.main.context_item.view.*
import kotlin.math.max

class ContextViewLinear @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ContextViewHolder {

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
        LayoutInflater.from(context).inflate(R.layout.context_item, this, true)
        delete_context.setOnClickListener {
            onDelete?.invoke(this)
        }
    }

    var onDelete: ((ContextViewHolder) -> Unit)? = null

    @Throws(AssertionError::class)
    override fun getBotContext(): Pair<String, String> {
        val key = context_key.text.toString()
        val value = context_value.text.toString()
        val blankKey = key.isBlank()
        val blankValue = value.isBlank()
        if (blankKey && !blankValue || blankValue && !blankKey) {
            throw AssertionError()
        }
        return key to value
    }

    override fun setBotContext(context: Pair<String, String>) {
        context_key.setText(context.first)
        context_value.setText(context.second)
    }

    override fun getView(): View {
        return this
    }
}

///////////////////////////////////////////////////

class ContextHandler(var container: ContextContainer, val contextsAdapter: ContextAdapter) {

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

    fun setContexts(contextPairs: List<Pair<String, String>>) {
        container.clear()
        contextPairs.forEach { pair ->
            addContext(pair)
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

interface ContextAdapter {
    fun createContextView(botContext: Pair<String, String>? = null,
                          onDelete: ((ContextViewHolder) -> Unit)? = null): View?
}

///////////////////////////////////////////////////

interface ContextViewHolder {
    @Throws(AssertionError::class)
    fun getBotContext(): Pair<String, String>

    fun setBotContext(context: Pair<String, String>)
    fun getView(): View
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

    override fun getContextList(): Map<String, String>? {
        return (0 until childCount - 1).map { idx ->
            val entry = (getChildAt(idx) as? ContextViewHolder)?.getBotContext() ?: Pair("", "")
            entry.first to entry.second
        }.filterNot { it.first.isBlank() }.toMap() // remove empty pairs
    }

    override fun getLast(): Pair<String, String>? {
        return (takeIf { childCount > 1 }?.getChildAt(childCount - 2) as? ContextViewHolder)?.getBotContext()
    }
}