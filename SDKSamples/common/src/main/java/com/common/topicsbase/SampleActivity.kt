package com.common.topicsbase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.common.utils.chat.ChatHolder
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.weakRef
import nanorep.com.common.R

abstract class SampleActivity  : AppCompatActivity() {

    private lateinit var singletonSamplesViewModelFactory: SingletonSamplesViewModelFactory

    private lateinit var viewModel: SamplesViewModel

    protected lateinit var chatProvider: ChatHolder

    protected lateinit var chatController: ChatController

    protected lateinit var topicTitle: String

    open lateinit var onChatLoaded: (fragment: Fragment) -> Unit

    protected open fun getAccount(): Account? = chatProvider.account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topicTitle = intent.getStringExtra("title") ?: ""

        singletonSamplesViewModelFactory =  SingletonSamplesViewModelFactory(
            SamplesViewModel.getInstance()
        )

        viewModel = ViewModelProvider(this, singletonSamplesViewModelFactory).get(SamplesViewModel::class.java)

        chatProvider = ChatHolder(baseContext.weakRef(), viewModel.accountProvider)

    }

    override fun onStop() {
        onSampleStop()
        super.onStop()
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