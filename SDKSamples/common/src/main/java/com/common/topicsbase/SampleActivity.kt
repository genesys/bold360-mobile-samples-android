package com.common.topicsbase

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.common.utils.forms.*
import com.common.utils.forms.defs.ChatType
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.SystemUtil
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.common.R

abstract class SampleActivity  : AppCompatActivity() {

    protected lateinit var topicTitle: String
    abstract val containerId: Int

//  <editor-fold desc=">>>>> Chat handling <<<<<" >

    /**
     * Returns encrypted info to be added to the Live account (if there is any)
     */
    protected fun getSecuredInfo(): String {
        return "some PGP encrypted key string [${SystemUtil.generateTimestamp()}]"
    }

//  </editor-fold>

//  <editor-fold desc=">>>>> Login forms handling <<<<<" >

    /**
     * Controls the Forms presentation
     */
    lateinit var accountController: AccountController

    open fun updateLoginData(loginData: LoginData) {
        loginData.account?.let { accountData = it }
    }

    var accountData: JsonObject = JsonObject()

    private val loginFormViewModel: LoginFormViewModel by viewModels()

    /**
     * Called after the LoginData had been updated from the ChatForm
     */
    abstract fun startChat(savedInstanceState: Bundle? = null)

    /**
     * Is being used as account saving key
     */
    @ChatType
    abstract val chatType: String

    abstract val account: Account?

  /*  *//**
     * Account data validation
     *//*
    open fun validateAccountData(): Boolean = true
    protected val presentError: (fieldIndex: Int, message: String) -> Unit = { index, message ->  accountController.presentError(index, message) }
*/
    lateinit var onChatTypeChanged: ((chatType: String) ->  Account)

    protected open val formFieldsData: JsonArray = JsonArray()


//  </editor-fold>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topicTitle = intent.getStringExtra("title").orEmpty()

        val loginFormViewModel: LoginFormViewModel by viewModels()

        accountController = AccountController(containerId, supportFragmentManager.weakRef(), JsonSharedDataHandler())

        loginFormViewModel.formFields = formFieldsData.applyValues(accountController.getSavedAccount(baseContext, chatType) as JsonObject)
//        3. Add validation to the formFields before loginData update

        accountController.presentForms()

        loginFormViewModel.loginData.observe(this, Observer<LoginData> { loginData->

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

    override fun onStop() {
        onSampleStop()
        super.onStop()
    }


    /**
     * Clears the chat and release its resources
     */
    abstract fun destructChat()

    protected open fun onSampleStop() {
        if (isFinishing) { destructChat() }
    }

//  </editor-fold>

}