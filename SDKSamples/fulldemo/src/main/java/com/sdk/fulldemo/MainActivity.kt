package com.sdk.fulldemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.common.topicsbase.FullDemo
import com.common.topicsbase.SamplesViewModel
import com.common.topicsbase.SingletonSamplesViewModelFactory
import com.common.utils.ERROR_DIALOG_REQUEST_CODE
import com.common.utils.accountUtils.ChatType
import com.common.utils.accountUtils.ExtraParams.NonSample
import com.common.utils.loginForms.AccountFormController
import com.common.utils.updateSecurityProvider
import com.nanorep.sdkcore.utils.weakRef
import kotlinx.android.synthetic.main.activity_main.*

/**
 * This app presents the Full Demo sample independently
 */
class MainActivity : AppCompatActivity() {

    private lateinit var singletonSamplesViewModelFactory: SingletonSamplesViewModelFactory
    private var retryProviderInstall = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val singletonSamplesViewModelFactory = SingletonSamplesViewModelFactory( SamplesViewModel.getInstance() )

        AccountFormController(demoContainer.id, supportFragmentManager.weakRef()).updateChatType(ChatType.None, listOf(NonSample)) { account, restoreState, extraData ->

            ViewModelProvider(this, singletonSamplesViewModelFactory).get(SamplesViewModel::class.java).apply {
                accountProvider.apply {
                    this.account = account
                    this.restoreState = restoreState
                    this.extraData = extraData
                }
            }

            showLoading(true)

            startActivity(Intent(this, FullDemo::class.java)
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

        if (::singletonSamplesViewModelFactory.isInitialized) {
            singletonSamplesViewModelFactory.clear()
        }

        super.onDestroy()
    }

    override fun onPostResume() {
        super.onPostResume()
        if (retryProviderInstall) {
            this.updateSecurityProvider()
            retryProviderInstall = false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
            retryProviderInstall = true
        }
    }
}