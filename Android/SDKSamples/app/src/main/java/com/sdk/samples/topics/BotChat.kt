package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.BotAccount

class BotChat : BasicChat() {
    override fun getAccount(): Account {
        return BotAccount(
            "",
            "nanorep",
            "English",
            ""
        )
    }
}