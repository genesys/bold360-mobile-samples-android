package com.sdk.samples.topics

import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.nanoengine.Account

open class BoldChatAsync : BasicChat() {

    protected val account:AsyncAccount by lazy {

        val userInfo = UserInfo("1234567654321234567")
        userInfo.firstName = "First name"
        userInfo.lastName = "Last name"
        userInfo.email = "Email@Bold.com"
        userInfo.phoneNumber = "123456"

        AsyncAccount("2307475884:2403340045369405:KCxHNTjbS7qDY3CVmg0Z5jqHIIceg85X:alphawd2", "mobile12345").apply {
            getInfo().userInfo = userInfo
        }

    }
    @JvmName("account") get

    override fun getAccount(): Account {
        return account
    }

}