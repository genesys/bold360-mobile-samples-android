package com.sdk.samples.topics

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.common.chatComponents.customProviders.withId
import com.common.topicsbase.SampleActivity
import com.common.utils.chatForm.defs.ChatType
import com.nanorep.convesationui.fragments.ArticleFragment
import com.nanorep.convesationui.structure.elements.Article
import com.nanorep.convesationui.views.autocomplete.AutocompleteViewUIConfig
import com.nanorep.convesationui.views.autocomplete.BotAutocompleteFragment
import com.nanorep.convesationui.views.autocomplete.BotCompletionViewModel
import com.nanorep.nanoengine.LinkedArticleHandler
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.nanoengine.model.configuration.StyleConfig
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.hideKeyboard
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.R
import com.sdk.samples.databinding.AutocompleteActivityBinding

class Autocomplete : SampleActivity<AutocompleteActivityBinding>() {

    override var chatType: String = ChatType.Bot

    override fun getViewBinding(): AutocompleteActivityBinding =
        DataBindingUtil.setContentView(this, R.layout.autocomplete_activity)


    override val containerId: Int
        get() = R.id.autocomplete_container

    override fun startSample(isStateSaved: Boolean) {

        (binding.samplesToolbar as? Toolbar)?.let {
            setSupportActionBar(it)
        }

        binding.articleView.setBackgroundColor(Color.parseColor("#88ffffff"))

        val botViewModel: BotCompletionViewModel by viewModels()

        //preserving existing chat session
        if (!botViewModel.botChat.hasSession) {
            botViewModel.botChat.account = (account as BotAccount).withId(this) // fixme: withId should be removed, functionality should be moved.
        }

        botViewModel.uiConfig = AutocompleteViewUIConfig(this).apply {
            inputStyleConfig = StyleConfig(16, Color.BLACK, Typeface.SERIF)
        }

        if (!isStateSaved) {
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

        (binding.samplesToolbar as? Toolbar)?.let {
            setSupportActionBar(it)
        }

        binding.topicTitle.text = topicTitle
    }

    private fun onError(error: NRError) {
        toast(baseContext, error.toString(), background = ColorDrawable(Color.RED))
    }

    /**
     * some visible action on article selection: we display the fetched article body in a "WebView"
     */
    private fun onArticle(article: Article) {
        var html = "<html><Style>${ArticleFragment.STYLE_TO_HANDLE_TABLES}</Style><body>${article.content
            ?: "Not Available"}</body></html>"
        html = LinkedArticleHandler.updateLinkedArticles(html)
        binding.articleView.loadData(html, "text/html", "UTF-8")
        binding.articleRoot.visibility = View.VISIBLE
        hideKeyboard(binding.articleRoot)
    }

}
