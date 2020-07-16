package com.sdk.samples.topics

import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.bot.BotAccount

object Accounts {

    private val formalBoldAccount: BoldAccount
        get() = BoldAccount("2300000001700000000:2278936004449775473:sHkdAhpSpMO/cnqzemsYUuf2iFOyPUYV") // Mobile

    private val fameBoldAccount: BoldAccount
        get() = BoldAccount("2300000001700000000:2279148490312878292:grCCPGyzmyITEocnaE+owvjtbasV16eV") // Fame

    val defaultBoldAccount: BoldAccount
        get() = fameBoldAccount// formalBoldAccount

    private val formalBotAccount: BotAccount
        get() = BotAccount(
            "",
            "nanorep",
            "English",
            "" //https://eu1-1.nanorep.com/console/login.html
        )

    private val testBotAccount: BotAccount
        get() = BotAccount(
            "", "nanorep",
            "English", "mobilestaging", null
        )

    val defaultBotAccount: BotAccount
        get() = testBotAccount///formalBotAccount

    val defaultAsyncAccount = AsyncAccount(
        "2300000001700000000:2279533687831071375:MlVOftOF/UFUUqPPSbMSDAnQjITxOrQW:gamma",
        "MobileAsyncStaging123452"
    ).apply {
        info.userInfo = UserInfo("1234567654321234569").apply {
            firstName = "First name"
            lastName = "Last name"
            email = "Email@Bold.com"
            phoneNumber = "123456"
        }
    }
}