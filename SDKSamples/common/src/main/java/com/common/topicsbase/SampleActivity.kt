package com.common.topicsbase

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.common.utils.chatForm.*
import com.common.utils.chatForm.FormDataFactory.addFormField
import com.common.utils.chatForm.defs.ChatType
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.common.R

abstract class SampleActivity : AppCompatActivity() {

    protected lateinit var topicTitle: String
    abstract val containerId: Int

//  <editor-fold desc=">>>>> Chat forms and account data handling <<<<<" >

    val loginFormViewModel: LoginFormViewModel by viewModels()

    lateinit var accountController: AccountController

    private var accountData: JsonObject = JsonObject()
    set(value) {
        field = value
        account = when (chatType) {
            ChatType.Live -> field.toLiveAccount()
            ChatType.Async -> field.toAsyncAccount()
            ChatType.Bot -> field.toBotAccount()
            else -> null
        }
    }

    private fun updateLoginData(loginData: LoginData) {
        loginData.account?.let { accountData = it }
    }

    protected fun getDataByKey(key: String): String? {
        return accountData.getString(key)
    }

    @ChatType
    abstract var chatType: String

    protected var account: Account? = null

    private val formFieldsData: JsonArray
    get() = FormDataFactory.createForm(chatType)

    open var extraDataFields: (() -> List<FormFieldFactory.FormField>) = { listOf() }

    /**
     * Called after the LoginData had been updated from the ChatForm
     */
    abstract fun startChat(savedInstanceState: Bundle? = null)

    protected fun presentForms() {
        formFieldsData.apply { extraDataFields().forEach { addFormField(it) } }.let {
            loginFormViewModel.formData = it.applyValues( accountController.getSavedAccount(baseContext, chatType) as JsonObject )
        }
        accountController.presentForms()
    }
//  </editor-fold>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topicTitle = intent.getStringExtra("title").orEmpty()

        val loginFormViewModel: LoginFormViewModel by viewModels()

        accountController = AccountController(containerId, supportFragmentManager.weakRef(), JsonSharedDataHandler())

        presentForms()

        loginFormViewModel.loginData.observe(this, Observer<LoginData> { loginData ->

            updateLoginData(loginData)

            accountController.saveAccount(baseContext, accountData, chatType)

            supportFragmentManager
                .popBackStack(
                    AccountFormPresenter.LOGIN_FORM,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

            startChat(savedInstanceState)

        })
    }

//  <editor-fold desc=">>>>> Base Activity actions <<<<<" >

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

//  </editor-fold>

}