package com.sdk.samples.topics

import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.handlers.AccountInfoProvider
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.sdkcore.utils.Completion

class BotChatDemo : BotChatHistory() {

    val accountProvider = object : AccountInfoProvider {
        override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {
            if (info.getApiKey() == Accounts.defaultAsyncAccount.getApiKey()) {
                callback.onComplete(
                    if (info.getApiKey() == Accounts.defaultAsyncAccount.getApiKey())
                        Accounts.defaultAsyncAccount
                    else
                        info)
            }
        }

        override fun update(account: AccountInfo) {
        }
    }

    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder().apply {
            accountProvider(accountProvider)
        }
    }
}