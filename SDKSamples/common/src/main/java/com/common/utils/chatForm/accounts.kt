package com.common.utils.chatForm

import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.bot.BotAccount
import java.util.UUID

object Accounts {

    private val formalBoldAccount: BoldAccount
        get() = BoldAccount("2300000001700000000:2278936004449775473:sHkdAhpSpMO/cnqzemsYUuf2iFOyPUYV") // Mobile

    private val fameBoldAccount: BoldAccount
        get() = BoldAccount("2300000001700000000:2279148490312878292:grCCPGyzmyITEocnaE+owvjtbasV16eV") // Fame

    private val QABoldAccount: BoldAccount
        get() = BoldAccount("3861082042421238616:3825974608601950887:FonKdKcfwvOAO3Npt4ml064Z/ysj1A+z") // Fame

    val defaultBoldAccount: BoldAccount
        get() = QABoldAccount

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

    val defaultBotAccount: BotAccount
        get() = testBotAccount

    val defaultAsyncAccount = AsyncAccount(
        "2300000001700000000:2279533687831071375:MlVOftOF/UFUUqPPSbMSDAnQjITxOrQW:gamma",
        "MobileAsyncStagingNew12345"
    ).apply {
        info.userInfo = UserInfo(UUID.randomUUID().toString()).apply {
            firstName = "Android"
            lastName = "Samples"
            email = "android.samples@bold.com"
            phoneNumber = "111-111-1111"
        }
    }
}