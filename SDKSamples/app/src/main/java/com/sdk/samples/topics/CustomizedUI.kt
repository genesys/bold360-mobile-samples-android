package com.sdk.samples.topics

import android.content.Context
import android.os.Bundle
import androidx.annotation.IntDef
import com.nanorep.convesationui.structure.providers.ChatUIProvider

const val override = 0
const val customize = 1

@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@IntDef(override, customize)
annotation class CustomType

open class CustomizedUI : BotChat() {

    @CustomType var customeType: Int? = null
    lateinit var uiProvider: ChatUIProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        customeType = intent.getIntExtra("type", override)

        uiProvider = UIProviderFactory.create(this)


    }
}

class UIProviderFactory() {

        companion object {

            fun create(context: Context): ChatUIProvider {
                return ChatUIProvider(context)
            }

        }

}