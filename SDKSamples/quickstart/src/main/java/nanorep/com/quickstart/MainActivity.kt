package nanorep.com.quickstart

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatEventListener
import com.nanorep.convesationui.structure.controller.ChatLoadResponse
import com.nanorep.convesationui.structure.controller.ChatLoadedListener
import com.nanorep.convesationui.structure.providers.ChatUIProvider
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.toast
import nanorep.com.quickstart.databinding.ActivityMainBinding

interface ChatFlowHandler : ChatEventListener {
    fun onAccountReady(account: AccountInfo, chatStartError: (() -> Unit)? = null)
    fun waitingVisibility(visible: Boolean)
}

class MainActivity : AppCompatActivity(), ChatFlowHandler {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var accountProvider = AccountHandler(false)
    private var boldController: ChatController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showFragment(AccountForm(), AccountForm.TAG)
    }

    override fun waitingVisibility(visible: Boolean) {
        binding.progressBar.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onAccountReady(account: AccountInfo, chatStartError: (() -> Unit)?) {
        accountProvider.update(account)
        createChat(account, chatStartError)
    }

    private fun createChat(account: AccountInfo, chatStartError: (() -> Unit)?) {
        waitingVisibility(true)

        boldController = ChatController.Builder(this).apply {

            conversationSettings(ConversationSettings())
            chatEventListener(this@MainActivity)

            // Here we apply the UI provider
            chatUIProvider(coxUIConfig())

        }.build(account, object : ChatLoadedListener {

            override fun onComplete(result: ChatLoadResponse) {

                val error = result.error ?: let {

                    if (result.fragment == null) {
                        NRError(NRError.EmptyError, "Chat UI failed to init")
                    } else {
                        null
                    }
                }

                error?.let {
                    onError(it)
                    chatStartError?.invoke()
                } ?: openConversationFragment(result.fragment!!)

                waitingVisibility(false)
            }
        })
    }

    private fun openConversationFragment(fragment: Fragment) {
        if (isFinishing || supportFragmentManager.isStateSaved ||
            supportFragmentManager.findFragmentByTag(CONVERSATION_FRAGMENT_TAG) != null
        ) {
            return
        }
        showFragment(fragment, CONVERSATION_FRAGMENT_TAG, true)
    }

    override fun onError(error: NRError) {
        super.onError(error)
        toast(this, error.description ?: error.reason ?: error.errorCode, Toast.LENGTH_SHORT)
    }

    private fun showFragment(fragment: Fragment, tag: String, addToBackStack: Boolean = false) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_main, fragment, tag).apply {
                if (addToBackStack) {
                    addToBackStack(tag)
                }
            }.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            boldController?.destruct()
        }
        super.onBackPressed()
    }

    companion object {
        const val CONVERSATION_FRAGMENT_TAG = "conversation_fragment"
    }


    private fun coxUIConfig() : ChatUIProvider = ChatUIProvider(this).apply {
        chatElementsUIProvider.incomingUIProvider.carouselUIProvider.configure = { adapter ->
            adapter.apply {
                setInfoSubTitleMinLines(4)
                setInfoTitleMinLines(3)
            }
        }
    }
}