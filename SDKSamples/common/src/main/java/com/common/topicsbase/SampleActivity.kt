package com.common.topicsbase

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.common.utils.chatForm.ChatForm
import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.JsonSampleRepository
import com.common.utils.chatForm.SampleData
import com.common.utils.chatForm.defs.ChatType
import com.nanorep.nanoengine.Account
import com.sdk.common.R

abstract class SampleActivity : AppCompatActivity() {

    protected lateinit var topicTitle: String
    abstract val containerId: Int

//  <editor-fold desc=">>>>> Chat forms and account data handling <<<<<" >

    val sampleFormViewModel: SampleFormViewModel by viewModels()

    val account: Account?
    get() = sampleFormViewModel.account

    protected fun getDataByKey(key: String): String? {
        return sampleFormViewModel.getAccountDataByKey(key)
    }

    @ChatType
    abstract var chatType: String

    /**
     * Called after the LoginData had been updated from the ChatForm
     */
    abstract fun startSample(savedInstanceState: Bundle? = null)

    open val extraDataFields: (() -> List<FormFieldFactory.FormField>)? = null

    protected fun presentSampleForm() {

        sampleFormViewModel.createFormFields(extraDataFields?.invoke())

        if (!supportFragmentManager.isStateSaved) {

            supportFragmentManager.beginTransaction()
                .replace(containerId, ChatForm.newInstance(), CHAT_FORM)
                .addToBackStack(CHAT_FORM)
                .commit()
        }

    }

//  </editor-fold>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topicTitle = intent.getStringExtra("title").orEmpty()

        val sampleFormViewModel: SampleFormViewModel by viewModels()

        sampleFormViewModel.updateChatType(chatType)

        sampleFormViewModel.sampleData.observe(this, Observer<SampleData> {

            supportFragmentManager
                .popBackStack(
                    CHAT_FORM,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

            startSample(savedInstanceState)

        })

        sampleFormViewModel.setRepository( JsonSampleRepository(applicationContext) )

        presentSampleForm()
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

    companion object {
        const val CHAT_FORM = "ChatForm"
    }

//  </editor-fold>

}