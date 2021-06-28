package com.sdk.samples.topics

import android.content.Context
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

enum class ConfigOption(val id : Int) {

    OVERRIDE(R.string.ui_override),
    CONFIGURE(R.string.ui_configuration) ,
    ARTICLE_CONFIG(R.string.ui_article_config);

    fun title(context: Context): String {
        return context.getString(id)
    }
}

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
            buttonSetup(it.configureOption, ConfigOption.CONFIGURE)
            buttonSetup(it.overrideOption, ConfigOption.OVERRIDE)
            buttonSetup(it.articleOption, ConfigOption.ARTICLE_CONFIG)
        }
    }

    private fun buttonSetup(button: Button, customUIOption: ConfigOption) {

        button.run {

            text = customUIOption.title(context)

            setOnClickListener {

                startActivity(Intent("com.sdk.sample.action.CUSTOMIZED_UI_IMPLEMENTATION").apply {
                    putExtra("type", customUIOption.toString())
                }.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))

                overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
        }
    }

}