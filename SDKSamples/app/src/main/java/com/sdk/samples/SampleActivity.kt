package com.sdk.samples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.samples.common.chat.ChatHolder

abstract class SampleActivity  : AppCompatActivity() {

    private lateinit var singletonSamplesViewModelFactory: SingletonSamplesViewModelFactory

    private lateinit var viewModel: SamplesViewModel

    protected lateinit var chatProvider: ChatHolder

    protected lateinit var chatController: ChatController

    protected open fun getAccount(): Account? = chatProvider.account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        singletonSamplesViewModelFactory =  SingletonSamplesViewModelFactory(
            SamplesViewModel.getInstance()
        )

        viewModel = ViewModelProvider(this, singletonSamplesViewModelFactory).get(SamplesViewModel::class.java)

        chatProvider = ChatHolder(baseContext.weakRef(), viewModel.accountProvider)
    }

    override fun onBackPressed() {

        super.onBackPressed()

        finishIfLast()
    }

    protected fun finishIfLast() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun finish() {
        chatProvider.clear()

        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }
}