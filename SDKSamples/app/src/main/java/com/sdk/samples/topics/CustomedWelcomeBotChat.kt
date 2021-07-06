package com.sdk.samples.topics

import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.R

class CustomedWelcomeBotChat : BotChat() {

    override val extraDataFields: (() -> List<FormFieldFactory.FormField>)
    get() = {
        listOf(
            FormFieldFactory.TextInputField( ChatType.Bot, DataKeys.Welcome, "", getString(R.string.form_hint_welcomeMessage), false )
        )
    }

    companion object{
        const val Customed_WM = "1009689562" //"1009687422"

        const val TestEnv_WM = "871383332"

        // use the following to prevent welcome message to appear on chat
        const val Disable_WM = BotAccount.None
    }
}
