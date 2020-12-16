package com.sdk.samples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.samples.SamplesViewModel

abstract class SampleActivity  : AppCompatActivity() {

    private lateinit var singletonSamplesViewModelFactory: SingletonSamplesViewModelFactory

    private lateinit var viewModel: SamplesViewModel

    protected lateinit var chatProvider: ChatProvider
    protected lateinit var accountProvider: AccountProvider

    protected lateinit var chatController: ChatController

    protected open fun getAccount(): Account? = accountProvider.account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        singletonSamplesViewModelFactory =  SingletonSamplesViewModelFactory(
            SamplesViewModel.getInstance{ applicationContext.weakRef() }
        )

        viewModel = ViewModelProvider(this, singletonSamplesViewModelFactory).get(SamplesViewModel::class.java)

        chatProvider = viewModel.chatProvider

        accountProvider = viewModel.accountProvider
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

}