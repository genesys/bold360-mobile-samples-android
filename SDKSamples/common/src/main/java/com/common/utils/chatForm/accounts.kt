package com.common.utils.chatForm

import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.bot.BotAccount
import java.util.UUID

object Accounts {

    private val formalBoldAccount: BoldAccount
        get() = BoldAccount("") // Mobile

    private val fameBoldAccount: BoldAccount
        get() = BoldAccount("") // Fame

    val defaultBoldAccount: BoldAccount
        get() = fameBoldAccount

    private val formalBotAccount: BotAccount
        get() = BotAccount(
            "",
            "",
            "",
            "" //https://eu1-1.nanorep.com/console/login.html
        )

    private val testBotAccount: BotAccount
        get() = BotAccount(
            "", "",
            "", "")

    val defaultBotAccount: BotAccount
        get() = testBotAccount

    val defaultAsyncAccount = AsyncAccount(
        "",
        ""
    ).apply {
        info.userInfo = UserInfo(UUID.randomUUID().toString()).apply {
            firstName = ""
            lastName = ""
            email = ""
            phoneNumber = ""
        }
    }
}