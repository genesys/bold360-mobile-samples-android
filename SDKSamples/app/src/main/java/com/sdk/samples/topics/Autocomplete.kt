package com.sdk.samples.topics

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nanorep.convesationui.fragments.ArticleFragment
import com.nanorep.convesationui.views.autocomplete.BotAutocompleteFragment
import com.nanorep.convesationui.views.autocomplete.BotCompletionViewModel
import com.nanorep.nanoengine.LinkedArticleHandler
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.nanoengine.model.ArticleResponse
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.R
import kotlinx.android.synthetic.main.autocomplete_activity.*

class Autocomplete : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.autocomplete_activity)

        article_view.setBackgroundColor(Color.parseColor("#88ffffff"))

        val botViewModel = ViewModelProviders.of(this).get(BotCompletionViewModel::class.java);
        //preserving existing chat session
        if (!botViewModel.botChat.hasSession) {
            botViewModel.botChat.account = BotAccount(
                "8bad6dea-8da4-4679-a23f-b10e62c84de8",
                "jio",
                "Staging_Updated",
                "qa07"
            )
            /*BotAccount(
                "",
                "nanorep",
                "English",
                "" //https://eu1-1.nanorep.com/console/login.html
            )*/
        }

        botViewModel.onError.observe(this, Observer { error ->
            toast(this@Autocomplete, error.toString(), background = ColorDrawable(Color.RED))
        })

        botViewModel.onSelection.observe(this, Observer { selection ->
            selection?.getArticle?.invoke(selection.articleId) { result ->
                result.error?.run { onError(this) } ?: result.data?.run { onArticle(this@run) }
            }
        })

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                supportFragmentManager.findFragmentByTag("autocompleteFrag")?.run { this@apply.attach(this) }?.commit()
                    ?: run {
                        add(R.id.root_layout, BotAutocompleteFragment(), "autocompleteFrag").commit()
                    }
            }
        }
    }

    private fun onError(error: NRError) {
        toast(this, error.toString(), background = ColorDrawable(Color.RED))
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
    }

}
