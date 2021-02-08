package com.sdk.samples.topics

import com.common.utils.forms.FormFieldFactory
import com.common.utils.forms.defs.DataKeys
import com.nanorep.nanoengine.bot.BotAccount

class CustomedWelcomeBotChat : BotChat() {

    override var extraDataFields: () -> List<FormFieldFactory.FormField> = {
        listOf(
            FormFieldFactory.TextInputField(DataKeys.Welcome, "", "Welcome message id", false)
        )
    }

    companion object{
        const val Customed_WM = "1009689562" //"1009687422"

        const val TestEnv_WM = "871383332"

        // use the following to prevent welcome message to appear on chat
        const val Disable_WM = BotAccount.None
    }
}
