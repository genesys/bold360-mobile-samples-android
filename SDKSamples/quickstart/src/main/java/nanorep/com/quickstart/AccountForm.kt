package nanorep.com.quickstart

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.bot.BotAccount
import nanorep.com.quickstart.databinding.AccountFormBinding

class AccountForm : Fragment() {

    private var _bind: AccountFormBinding? = null
    private val binding get() = _bind!!

    private var chatFlowHandler: ChatFlowHandler? = null

    private var account: AccountInfo? = null
    private val prevDataHandler = PrevDataHandler()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        chatFlowHandler = (context as? ChatFlowHandler) ?: kotlin.run {
            Log.e("AccountForm", "$context must implement ChatFlowHandler")
            null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _bind = AccountFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startChat.setOnClickListener { view ->

            view.startAnimation(AlphaAnimation(1f, 0.8f).also { it.duration = 150 })

            account = createAccount()
            account?.run {
                view.isEnabled = false
                chatFlowHandler?.onAccountReady(this){
                    binding.startChat.isEnabled = true
                }
            }
        }

        fillFields()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onStop() {
        super.onStop()

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.takeIf { view != null }?.run { imm.hideSoftInputFromWindow(view!!.windowToken, 0) }
    }

    private fun createAccount(): AccountInfo? {
        return getBotAccount()
    }

    /**
     * retrieve last saved form data from shared preferences and fill the relevant fields
     */
    private fun fillFields() {
        if (context == null) return

        val data = account?.let {
            (it as? BotAccount)?.map()
        } ?: prevDataHandler.getFormData(requireContext())

        binding.accountNameEditText.setText(data[PrevDataHandler.Account_key] as? String ?: "")

        binding.knowledgebaseEditText.setText(data[PrevDataHandler.Kb_key] as? String ?: "")

        binding.serverEditText.setText(data[PrevDataHandler.Server_key] as? String ?: "")

        binding.apiKeyEditText.setText(
            (data[PrevDataHandler.ApiKey_key] as? String) ?: ""
        )
    }

    private fun getBotAccount(): BotAccount? {
        try {
            val accountName = binding.accountNameEditText.text.toString()
            val kb = binding.knowledgebaseEditText.text.toString()
            val apiKey = binding.apiKeyEditText.text.toString()
            val server = binding.serverEditText.text.toString()

            if (accountName.isBlank() || kb.isBlank()/* || server.isBlank()*/) {

                when {
                    accountName.isBlank() -> {
                        binding.accountNameEditText.apply {
                            requestFocus()
                            error = context.getString(R.string.empty_account_name_error)
                        }
                    }
                    kb.isBlank() -> {
                        binding.knowledgebaseEditText.apply {
                            requestFocus()
                            binding.knowledgebaseEditText.error = context.getString(R.string.empty_knowlegebase_error)
                        }
                    }
                }

                throw AssertionError("Missing bot fields")
            }

            val contexts: MutableMap<String, String>? = binding.accountContextEditText.text.toString().replace(" ","").takeIf { it.isNotEmpty() }?.let {

                 mutableMapOf<String, String>().apply {
                     StringBuilder(it).let { contextBuilder ->

                         if (contextBuilder.last() != ';') contextBuilder.append(';')

                         contextBuilder.split(';').forEach { contexts ->
                                 it.split(':').let { singleContext ->
                                     put(singleContext.first(), singleContext.last())
                                 }
                             }
                     }
                 }
            }

            val botAccount = BotAccount(apiKey, accountName, kb, server, contexts)

            context?.run {
                prevDataHandler.saveChatData(this, botAccount)
            }

            return botAccount
        } catch (ast: AssertionError) {
            return null
        }
    }

    override fun onResume() {
        super.onResume()
        binding.startChat.isEnabled = true
    }

    companion object {
        const val TAG = "AccountForm_Tag"
    }
}
