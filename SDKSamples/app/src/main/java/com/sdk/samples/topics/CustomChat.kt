package com.sdk.samples.topics

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.sdk.samples.R
import kotlinx.android.synthetic.main.activity_bot_chat.*


open class CustomChat : AppCompatActivity() {

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

            val buttonStyle = R.style.main_button
            val intent = Intent("com.sdk.sample.action.CUSTOMIZE_UI")

            buttonsContainer.addView(Button(ContextThemeWrapper(this, buttonStyle), null, buttonStyle).apply {
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    setMargins(0,0,0,30)
                }

                text = getString(R.string.cutomized_ui, configure)

                setOnClickListener {

                    startActivity(intent.apply {
                        putExtra("title", text.toString())
                        putExtra("type", configure)
                    })

                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            })

            buttonsContainer.addView(Button(ContextThemeWrapper(this, buttonStyle), null,buttonStyle).apply {

                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }

                text = getString(R.string.cutomized_ui, override)

                setOnClickListener {

                    startActivity(intent.apply {
                        putExtra("title", text.toString())
                        putExtra("type", override)
                    })

                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            })

            chat_view.addView(buttonsContainer)

        }
    }

}