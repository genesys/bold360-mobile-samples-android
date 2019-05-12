package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.BotAccount

class BotChat : BasicChat() {
    override fun getAccount(): Account {
        /*return BotAccount(
            "8bad6dea-8da4-4679-a23f-b10e62c84de8",
            "jio",
            "Staging_Updated",
            "qa07"
        )*/
        return BotAccount(
            "",
            "nanorep",
            "English",
            ""
        )
    }
}