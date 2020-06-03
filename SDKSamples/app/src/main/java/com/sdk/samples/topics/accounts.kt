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
        get() = formalBoldAccount

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
            "English", "mobilestaging")

    private val rbsBotAccount: BotAccount
        get() = BotAccount("252f4e19-7ebd-4ebc-8f20-f28778361899", "rbspilot",
            "English", "eu1-1")

    val defaultBotAccount: BotAccount
        get() = rbsBotAccount


    private val formalAsyncAccount = AsyncAccount(
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

    private val rbsAsyncAccount = AsyncAccount(
        "700923217738955243:696490953063305782:60/4G71sRGPWY/A8g4yxFeNTSQKcMtxr:eu",
        "RbsPilotMobileMessaging").apply {
        info.userInfo = UserInfo("7009232177389552433").apply {
            firstName = "RBS"
            lastName = "Demo"
        }
    }

    val defaultAsyncAccount: AsyncAccount
        get() = rbsAsyncAccount
}