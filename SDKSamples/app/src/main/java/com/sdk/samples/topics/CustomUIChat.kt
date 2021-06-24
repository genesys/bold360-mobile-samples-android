package com.sdk.samples.topics

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sdk.common.databinding.ActivityBasicBinding
import com.sdk.samples.R
import com.sdk.samples.databinding.CustomUiOptionsLayoutBinding

open class CustomUIChat : AppCompatActivity() {

    private lateinit var binding: ActivityBasicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this, com.sdk.common.R.layout.activity_basic)

        setSupportActionBar(findViewById(R.id.sample_toolbar))

        binding.topicTitle.text = intent.getStringExtra("title")

        initOptionsView()

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }


    private fun initOptionsView(){
        binding.basicLoading.visibility = View.GONE
        CustomUiOptionsLayoutBinding.inflate(LayoutInflater.from(baseContext), binding.basicChatView , true).let {
            buttonSetup(it.configureOption, configure)
            buttonSetup(it.overrideOption, override)
            buttonSetup(it.articleOption, articleConfig)
        }
    }

    private fun buttonSetup(button: Button, @CustomUIOption customUIOption: String) {

        button.run {

            text = customUIOption

            setOnClickListener {

                startActivity(Intent("com.sdk.sample.action.CUSTOMIZED_UI_IMPLEMENTATION").apply {

                    putExtra("title", text.toString())
                    putExtra("type", customUIOption)
                }.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))

                overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
        }
    }

}