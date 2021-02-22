package com.sdk.samples.topics

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.common.chatComponents.customProviders.withId
import com.common.topicsbase.SampleActivity
import com.common.utils.chatForm.defs.ChatType
import com.nanorep.convesationui.fragments.ArticleFragment
import com.nanorep.convesationui.views.autocomplete.AutocompleteViewUIConfig
import com.nanorep.convesationui.views.autocomplete.BotAutocompleteFragment
import com.nanorep.convesationui.views.autocomplete.BotCompletionViewModel
import com.nanorep.nanoengine.LinkedArticleHandler
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.nanoengine.model.ArticleResponse
import com.nanorep.nanoengine.model.configuration.StyleConfig
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.hideKeyboard
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.R
import kotlinx.android.synthetic.main.autocomplete_activity.article_root
import kotlinx.android.synthetic.main.autocomplete_activity.article_view

class Autocomplete : SampleActivity() {

    override var chatType: String = ChatType.Bot

    override val containerId: Int
        get() = R.id.autocomplete_container

    override fun startSample(savedInstanceState: Bundle?) {
        setSupportActionBar(findViewById(R.id.sample_toolbar))
        article_view.setBackgroundColor(Color.parseColor("#88ffffff"))

        val botViewModel: BotCompletionViewModel by viewModels()

        //preserving existing chat session
        if (!botViewModel.botChat.hasSession) {
            botViewModel.botChat.account = (account as BotAccount).withId(this)
        }

        botViewModel.uiConfig = AutocompleteViewUIConfig(this).apply {
            inputStyleConfig = StyleConfig(16, Color.BLACK, Typeface.SERIF)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                supportFragmentManager.findFragmentByTag("autocompleteFrag")?.run { this@apply.attach(this) }?.commit()
                    ?: run {
                        add(R.id.autocomplete_view, BotAutocompleteFragment(), "autocompleteFrag").commit()
                    }
            }
        }

        botViewModel.onError.observe(this, Observer { error ->
            toast(baseContext, error.toString(), background = ColorDrawable(Color.RED))
        })

        botViewModel.onSelection.observe(this, Observer { selection ->
            selection?.getArticle?.invoke(selection.articleId) { result ->
                result.error?.run { onError(this) } ?: result.data?.run { onArticle(this@run) }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.autocomplete_activity)
        setSupportActionBar(findViewById(com.sdk.common.R.id.sample_toolbar))

        findViewById<TextView>(R.id.topic_title).text = topicTitle
    }

    private fun onError(error: NRError) {
        toast(baseContext, error.toString(), background = ColorDrawable(Color.RED))
    }

    /**
     * some visible action on article selection: we display the fetched article body in a "WebView"
     */
    private fun onArticle(articleResponse: ArticleResponse) {
        var html = "<html>${ArticleFragment.STYLE_TO_HANDLE_TABLES}<body>${articleResponse.body
            ?: "Not Available"}</body></html>"
        html = LinkedArticleHandler.updateLinkedArticles(html)
        article_view.loadData(html, "text/html", "UTF-8")
        article_root.visibility = View.VISIBLE
        hideKeyboard(article_root)
    }

}
