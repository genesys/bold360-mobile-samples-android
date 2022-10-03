package com.sdk.samples.topics

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.common.topicsbase.SampleActivity
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.toast
import com.nanorep.convesationui.structure.elements.Article
import com.nanorep.convesationui.views.ArticleUIConfig
import com.nanorep.convesationui.views.autocomplete.AutocompleteViewUIConfig
import com.nanorep.convesationui.views.autocomplete.BotAutocompleteFragment
import com.nanorep.convesationui.views.autocomplete.BotCompletionViewModel
import com.nanorep.nanoengine.LinkedArticleHandler
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.nanoengine.model.configuration.StyleConfig
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.hideKeyboard
import com.sdk.samples.R
import com.sdk.samples.databinding.AutocompleteActivityBinding

class Autocomplete : SampleActivity<AutocompleteActivityBinding>() {

    override var chatType: String = ChatType.Bot

    override fun getViewBinding(): AutocompleteActivityBinding =
        DataBindingUtil.setContentView(this, R.layout.autocomplete_activity)


    override val containerId: Int
        get() = R.id.autocomplete_container

    override fun startSample() {

        (binding.samplesToolbar as? Toolbar)?.let {
            setSupportActionBar(it)
        }

        binding.articleView.setBackgroundColor(Color.parseColor("#88ffffff"))

        val botViewModel: BotCompletionViewModel by viewModels()

        //preserving existing chat session
        // Configuring a custom account provider that supports continuity :
        if (!botViewModel.botChat.hasSession) {
            ( sampleFormViewModel.continuityRepository.getSessionToken("botUserId_${this.account}") )?.let {
                botViewModel.botChat.account = (account as BotAccount).apply { userId = it}
            }
        }

        botViewModel.uiConfig = AutocompleteViewUIConfig(this).apply {
            inputStyleConfig = StyleConfig(16, Color.BLACK, Typeface.SERIF)
        }

        if (!supportFragmentManager.isStateSaved) {
            supportFragmentManager.beginTransaction().apply {
                supportFragmentManager.findFragmentByTag("autocompleteFrag")?.run { this@apply.attach(this) }?.commit()
                    ?: run {
                        add(R.id.autocomplete_view, BotAutocompleteFragment(), "autocompleteFrag").commit()
                    }
            }
        }

        botViewModel.onError.observe(this, Observer { error ->
            error?.run { onError(this) }
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
        toast(error.toString(), Toast.LENGTH_SHORT)
        if (error.errorCode == NRError.ConversationCreationError) finish()
    }

    /**
     * some visible action on article selection: we display the fetched article body in a "WebView"
     */
    private fun onArticle(article: Article) {
        var html = "<html><Style>${ArticleUIConfig.TableCssStyle}</Style><body>${article.content}</body></html>"
        html = LinkedArticleHandler.updateLinkedArticles(html)
        binding.articleView.loadData(html, "text/html", "UTF-8")
        binding.articleRoot.visibility = View.VISIBLE
        hideKeyboard(binding.articleRoot)
    }

}
