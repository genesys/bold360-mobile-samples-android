package com.sdk.samples.topics

import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.nanoengine.Account

open class BoldChatAsync : BasicChat() {

    protected val account:AsyncAccount by lazy {
        defaultAsyncAccount
    }

    @JvmName("account") get
    override fun getAccount(): Account {
        return account
    }

    companion object{
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
}