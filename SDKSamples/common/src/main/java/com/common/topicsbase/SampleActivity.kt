package com.common.topicsbase

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.common.utils.chat.ChatHolder
import com.common.utils.loginForms.AccountFormController
import com.common.utils.loginForms.AccountFormPresenter
import com.common.utils.loginForms.LoginFormViewModel
import com.common.utils.loginForms.accountUtils.ChatType
import com.common.utils.loginForms.accountUtils.FormsParams
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.common.R

abstract class SampleActivity  : AppCompatActivity() {

    @ChatType
    abstract val chatType: String
    abstract val containerId: Int

    open var formsParams: Int
    set(value) {
        loginFormViewModel.formsParams = value
    }
    get() = loginFormViewModel.formsParams


    abstract fun startChat(savedInstanceState: Bundle? = null)

    protected lateinit var chatProvider: ChatHolder
    protected lateinit var chatController: ChatController
    protected lateinit var topicTitle: String

    private lateinit var accountFormController: AccountFormController

    private val loginFormViewModel: LoginFormViewModel by viewModels()

    abstract val onChatLoaded: (fragment: Fragment) -> Unit

    protected open fun getAccount(): Account? = loginFormViewModel.getAccount(baseContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topicTitle = intent.getStringExtra("title") ?: ""

        val loginFormViewModel: LoginFormViewModel by viewModels()

        chatProvider = ChatHolder(baseContext.weakRef(), onChatLoaded)

        accountFormController = AccountFormController(containerId, supportFragmentManager.weakRef())

        loginFormViewModel.formsParams = formsParams

        accountFormController.updateChatType(chatType)

        loginFormViewModel.loginData.observe(this, { accountData ->

            chatProvider.loginData = accountData

            supportFragmentManager
                .popBackStack(
                    AccountFormPresenter.LOGIN_FORM,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

            startChat(savedInstanceState)

        })
    }

    protected fun hasChatController() = chatProvider.hasChatController()

    override fun onStop() {
        onSampleStop()
        super.onStop()
    }

    fun addFormsParam(@FormsParams param: Int) {
        loginFormViewModel.formsParams = loginFormViewModel.formsParams or param
    }

    protected open fun onSampleStop() {
        if (isFinishing) { chatProvider.destruct() }
    }

    override fun onBackPressed() {

        super.onBackPressed()

        supportFragmentManager.executePendingTransactions()

        if (!isFinishing) { finishIfLast() }
    }

    protected fun finishIfLast() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }
}