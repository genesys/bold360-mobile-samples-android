package com.sdk.fulldemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.common.topicsbase.FullDemoSample
import com.common.topicsbase.SamplesViewModel
import com.common.topicsbase.SingletonSamplesViewModelFactory
import com.common.utils.accountUtils.ChatType
import com.common.utils.loginForms.AccountFormController
import com.nanorep.sdkcore.utils.weakRef
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var singletonSamplesViewModelFactory: SingletonSamplesViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val singletonSamplesViewModelFactory = SingletonSamplesViewModelFactory( SamplesViewModel.getInstance() )

        AccountFormController(demoContainer.id, supportFragmentManager.weakRef()).updateChatType(ChatType.None, listOf()) { account, restoreState, extraData ->

            ViewModelProvider(this, singletonSamplesViewModelFactory).get(SamplesViewModel::class.java).apply {
                accountProvider.apply {
                    this.account = account
                    this.restoreState = restoreState
                    this.extraData = extraData
                }
            }

            showLoading(true)

            startActivity(Intent(this, FullDemoSample::class.java)
                .putExtra("isSample", false)
                .putExtra("title", "Full Demo"))

            overridePendingTransition(R.anim.right_in, R.anim.left_out)

        }
    }
    private fun showLoading(show: Boolean) {
        if (show) {
            demoLoading.visibility = View.VISIBLE
            demoContainer.visibility = View.GONE
        } else {
            demoLoading.visibility = View.GONE
            demoContainer.visibility = View.VISIBLE
        }
    }

    override fun onRestart() {
        super.onRestart()
        showLoading(false)
    }

    override fun onDestroy() {

        singletonSamplesViewModelFactory.clear()

        super.onDestroy()
    }
}