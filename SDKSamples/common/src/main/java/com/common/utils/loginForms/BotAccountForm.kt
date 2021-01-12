package com.common.utils.loginForms

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import com.common.utils.loginForms.BotSharedDataHandler.Companion.Account_key
import com.common.utils.loginForms.BotSharedDataHandler.Companion.ApiKey_key
import com.common.utils.loginForms.BotSharedDataHandler.Companion.Context_key
import com.common.utils.loginForms.BotSharedDataHandler.Companion.Kb_key
import com.common.utils.loginForms.BotSharedDataHandler.Companion.Server_key
import com.common.utils.loginForms.accountUtils.ChatType
import com.common.utils.loginForms.accountUtils.ExtraParams.*
import com.common.utils.loginForms.accountUtils.isEmpty
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.px
import com.sdk.common.R
import kotlinx.android.synthetic.main.bot_account_form.*
import kotlinx.android.synthetic.main.bot_context_view.view.*
import kotlin.math.max

class BotAccountForm : AccountForm(),
    ContextAdapter {

    override val formLayoutRes: Int
        get() = R.layout.bot_account_form

    override val chatType: String
        get() = ChatType.Bot

    private lateinit var contextHandler: ContextHandler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (hasFormParam(Welcome)) bot_welcome_layout.visibility = View.VISIBLE
        if (hasFormParam(UsingContext)) bot_context.visibility = View.VISIBLE
        if (hasFormParam(PrechatExtraData))bot_prechat_data.visibility = View.VISIBLE

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeContextView() {

        bot_context.scroller = scroller

        contextHandler = ContextHandler(bot_context, this@BotAccountForm).apply {
            onDelete = { _ ->
                if (bot_context.childCount == 1) {
                    bot_context_title.visibility = View.GONE
                }
            }
        }

        add_context.setOnClickListener {
            try {
                val lastContext = contextHandler.container.getLast()
                if (lastContext == null || !lastContext.isEmpty()) {
                    if (lastContext == null) {
                        bot_context_title.visibility = View.VISIBLE
                    }

                    contextHandler.addContext()
                }
            } catch (ast: AssertionError) {
                Log.w("AccountForm", "got assertion error")
            }
        }
    }

    override fun fillFields() {

        initializeContextView()

        val account: BotAccount = loginFormViewModel.getAccount(context) as BotAccount

        bot_account_name_edit_text.setText( account.account ?: "" )
        bot_knowledgebase_edit_text.setText( account.knowledgeBase ?: "" )
        bot_api_key_edit_text.setText( account.apiKey )
        bot_server_edit_text.setText( account.domain ?: "" )

        bot_context.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int,
                                        oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                scroller.scrollTo(0, 0)
                bot_context.removeOnLayoutChangeListener(this)
            }
        })

        (account.contexts as? Set<String>)?.run {
            val contextPairs = this.map { contextString ->
                val seq = contextString.split(":", "key= ", " value= ")
                seq[0] to Pair(seq[2], seq.last())
            }
                .sortedBy { k -> k.first }
                .map { t -> t.second }

            bot_context_title.visibility = if (contextPairs.isEmpty()) View.GONE else View.VISIBLE

            contextHandler.setContexts(contextPairs)
        }
    }

    override fun validateFormData(): Map<String, Any?>? {

        val accountMap = mutableMapOf<String, Any?>()

        bot_account_name_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[Account_key] = it.toString()
        } ?: kotlin.run {
            presentError(bot_account_name_edit_text, context?.getString(R.string.error_account_name))
            return null
        }

        bot_knowledgebase_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[Kb_key] = it.toString()
        } ?: kotlin.run {
            presentError(bot_knowledgebase_edit_text, context?.getString(R.string.error_kb))
            return null
        }

        accountMap[ApiKey_key] = bot_api_key_edit_text.text?.toString() ?: ""
        accountMap[Server_key] = bot_server_edit_text.text?.toString() ?: ""
        accountMap[Context_key] =  contextHandler.getContext()

        bot_welcome_edit_text.text?.takeUnless { it.isEmpty() }?.let { accountMap[BotSharedDataHandler.Welcome_key] = it }
        bot_prechat_dept_edit_text.text?.takeUnless { it.isEmpty() }?.let { accountMap[BotSharedDataHandler.preChat_deptCode_key] = it }
        bot_prechat_fName_edit_text.text?.takeUnless { it.isEmpty() }?.let { accountMap[BotSharedDataHandler.preChat_fName_key] = it }
        bot_prechat_lName_edit_text.text?.takeUnless { it.isEmpty() }?.let { accountMap[BotSharedDataHandler.preChat_lName_key] = it }

        return accountMap
    }


    companion object {
        fun newInstance(): BotAccountForm {
            return BotAccountForm()
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
