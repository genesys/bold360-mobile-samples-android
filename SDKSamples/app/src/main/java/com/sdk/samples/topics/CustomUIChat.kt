package com.sdk.samples.topics

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.sdk.samples.R
import kotlinx.android.synthetic.main.activity_basic.*
import kotlinx.android.synthetic.main.custom_ui_options_layout.*


open class CustomUIChat : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        topic_title.text = intent.getStringExtra("title")

        initOptionsView()

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }


    private fun initOptionsView(){
        basic_loading.visibility = View.GONE
        LayoutInflater.from(this).inflate(R.layout.custom_ui_options_layout, basic_chat_view, true)
        buttonSetup(configure_option, configure)
        buttonSetup(override_option, override)
    }

    private fun buttonSetup(button: Button, @CustomUIOption customUIOption: String) {

        button.run {

            text = getString(R.string.cutomized_ui, customUIOption)

            setOnClickListener {

                startActivity(Intent("com.sdk.sample.action.CUSTOMIZED_UI_IMPLEMENTATION").apply {

                    putExtra("title", text.toString())
                    putExtra("type", customUIOption)
                }.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))

                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        }
    }

}