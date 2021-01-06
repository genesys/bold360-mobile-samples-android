package com.sdk.fulldemo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.common.topicsbase.FullDemoSample
import com.common.topicsbase.SamplesViewModel
import com.common.topicsbase.SingletonSamplesViewModelFactory
import com.common.utils.loginForms.accountUtils.ExtraParams

internal class FullDemo : FullDemoSample() {

    override val extraFormsParams = super.extraFormsParams.apply { add(ExtraParams.NonSample) }

    /*override var extraParams = super.extraParams.apply {
        add(ExtraParams.NonSample)
    }*/

    private var singletonSamplesViewModelFactory = SingletonSamplesViewModelFactory( SamplesViewModel.getInstance() )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<TextView>(R.id.topic_title).visibility = View.GONE
    }

    // Avoids sample finish animation:
    override fun overridePendingTransition(enterAnim: Int, exitAnim: Int) {}

    // Clears the used view holder
    override fun onDestroy() {
        singletonSamplesViewModelFactory.clear()
        super.onDestroy()
    }
}