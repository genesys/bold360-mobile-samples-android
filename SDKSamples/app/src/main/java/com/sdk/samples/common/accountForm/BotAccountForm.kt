package com.sdk.samples.common.accountForm

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.px
import com.sdk.samples.R
import com.sdk.samples.common.BotSharedDataHandler.Companion.Account_key
import com.sdk.samples.common.BotSharedDataHandler.Companion.ApiKey_key
import com.sdk.samples.common.BotSharedDataHandler.Companion.Context_key
import com.sdk.samples.common.BotSharedDataHandler.Companion.Kb_key
import com.sdk.samples.common.BotSharedDataHandler.Companion.Server_key
import com.sdk.samples.common.DataController
import com.sdk.samples.common.isEmpty
import kotlinx.android.synthetic.main.bot_account_form.*
import kotlinx.android.synthetic.main.bot_context_view.view.*
import kotlin.math.max

class BotAccountForm(dataController: DataController) : AccountForm(dataController), ContextAdapter {

    override val formLayoutRes: Int
        get() = R.layout.bot_account_form

    private lateinit var contextHandler: ContextHandler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeContextView()
    }

    private fun initializeContextView() {

        bot_context.scroller = scroller

        ContextHandler(bot_context, this@BotAccountForm).apply {
            onDelete = { _ ->
                if (bot_context.childCount == 1) {
                    context_title.visibility = View.GONE
                }
            }
        }

        add_context.setOnClickListener {
            try {
                val lastContext = contextHandler.container.getLast()
                if (lastContext == null || !lastContext.isEmpty()) {
                    if (lastContext == null) {
                        context_title.visibility = View.VISIBLE
                    }

                    contextHandler.addContext()
                }
            } catch (ast: AssertionError) {
                Log.w("AccountForm", "got assertion error")
            }
        }
    }

    override fun fillFields() {

        val accountData = dataController.getAccount(context)

        account_name_edit_text.setText( accountData[Account_key] as? String ?: "" )
        knowledgebase_edit_text.setText( accountData[Kb_key] as? String ?: "" )
        api_key_edit_text.setText( accountData[ApiKey_key] as? String ?: "" )
        server_edit_text.setText( accountData[Server_key] as? String ?: "" )

        bot_context.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int,
                                        oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                scroller.scrollTo(0, 0)
                bot_context.removeOnLayoutChangeListener(this)
            }
        })

        (accountData[Context_key] as? Set<String>)?.run {
            val contextPairs = this.map { contextString ->
                val seq = contextString.split(":", "key= ", " value= ")
                seq[0] to Pair(seq[2], seq.last())
            }
                .sortedBy { k -> k.first }
                .map { t -> t.second }

            context_title.visibility = if (contextPairs.isEmpty()) View.GONE else View.VISIBLE

            contextHandler.setContexts(contextPairs)
        }
    }

    override fun validateFormData(): Account? {

        val accountName = account_name_edit_text?.let { accountNameView ->
            accountNameView.text.takeUnless { it.isEmpty() }?.toString()  ?: kotlin.run {
                presentError(accountNameView, context?.getString(R.string.error_account_name))
                return null
            }}

        val kb = knowledgebase_edit_text?.let { kbView ->
            kbView.text?.takeUnless { it.isEmpty() }?.toString()  ?: kotlin.run {
                presentError(kbView, context?.getString(R.string.error_kb))
                return null
            }
        }

        val apiKey = api_key_edit_text.text?.toString() ?: ""
        val server = server_edit_text.text?.toString() ?: ""
        val contexts = contextHandler.getContext()

        return BotAccount(apiKey, accountName, kb, server, contexts)
    }


    companion object {
        fun newInstance(dataController: DataController): BotAccountForm {
            return BotAccountForm(dataController)
        }
    }

    override fun createContextView(botContext: Pair<String, String>?, onDelete: ((ContextViewHolder) -> Unit)?): View? {
        return context?.run {
            ContextViewLinear(this).apply {
                this.onDelete = onDelete
                botContext?.run {
                    this@apply.setBotContext(botContext)
                }
            }
        }
    }
}
///////////////////////////////////////////////////

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
        orientation = LinearLayout.HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.bot_context_view, this, true)

        delete_context.setOnClickListener { view ->
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
