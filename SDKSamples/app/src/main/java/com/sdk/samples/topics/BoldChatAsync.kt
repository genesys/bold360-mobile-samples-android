package com.sdk.samples.topics

import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.nanoengine.Account

open class BoldChatAsync : BasicChat() {

    protected val account:AsyncAccount by lazy {

        AsyncAccount("2307475884:2403340045369405:KCxHNTjbS7qDY3CVmg0Z5jqHIIceg85X:alphawd2", "mobile12345").apply {

            info.userInfo = UserInfo("1234567654321234569").apply {
                firstName = "First name"
                lastName = "Last name"
                email = "Email@Bold.com"
                phoneNumber = "123456"
            }

        }
    }

    @JvmName("account") get

    override fun getAccount(): Account {
        return account
    }

}