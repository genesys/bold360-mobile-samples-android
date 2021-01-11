package com.sdk.fulldemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.common.utils.ERROR_DIALOG_REQUEST_CODE
import com.common.utils.updateSecurityProvider

/**
 * This app presents the Full Demo sample independently
 */
class MainActivity : AppCompatActivity() {

//    private lateinit var singletonSamplesViewModelFactory: SingletonSamplesViewModelFactory
    private var retryProviderInstall = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.sample_toolbar))

       /* val singletonSamplesViewModelFactory = SingletonSamplesViewModelFactory( SamplesViewModel.getInstance() )

        AccountFormController(demoContainer.id, supportFragmentManager.weakRef()).updateChatType(ChatType.None, listOf(AsyncExtraData, UsingContext, Welcome, NonSample)) { account, restoreState, extraData ->

            ViewModelProvider(this, singletonSamplesViewModelFactory).get(SamplesViewModel::class.java).apply {
                accountProvider.apply {
                    this.account = account
                    this.restoreState = restoreState
                    this.extraData = extraData
                }
            }

            supportFragmentManager.fragments.forEach { _ ->
                supportFragmentManager.popBackStackImmediate()
            }

            demoLoading.visibility = View.VISIBLE
            demoContainer.visibility = View.GONE

            startActivity(Intent(this, FullDemo::class.java))
            overridePendingTransition(R.anim.right_in, R.anim.left_out)

        }*/

    }

    override fun onDestroy() {

    /*    if (::singletonSamplesViewModelFactory.isInitialized) {
            singletonSamplesViewModelFactory.clear()
        }*/

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