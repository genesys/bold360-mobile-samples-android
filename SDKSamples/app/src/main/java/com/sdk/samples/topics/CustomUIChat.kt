package com.sdk.samples.topics

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import com.nanorep.sdkcore.utils.px
import com.sdk.samples.R
import kotlinx.android.synthetic.main.activity_bot_chat.*


open class CustomUIChat : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bot_chat)

        topic_title.text = intent.getStringExtra("title")

        LinearLayout(this).apply {
            
            layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
            orientation = LinearLayout.VERTICAL

        }.also { buttonsContainer ->

            addButton(buttonsContainer, configure)

            addButton(buttonsContainer, override)

            chat_view.addView(buttonsContainer)

        }
    }

    private fun addButton(buttonsContainer: ViewGroup, @CustomUIOption customUIOption: String) {

        val buttonStyle = R.style.main_button

        buttonsContainer.addView(Button(ContextThemeWrapper(this, buttonStyle), null, buttonStyle).apply {

            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setMargins(10.px)
            }

            text = getString(R.string.cutomized_ui, customUIOption)

            setOnClickListener {

                startActivity(Intent("com.sdk.sample.action.CUSTOMIZED_UI_IMPLEMENTATION").apply {
                    putExtra("title", text.toString())
                    putExtra("type", customUIOption)
                })

                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

}