package com.sdk.samples.topics

import androidx.annotation.NonNull
import com.integration.core.annotations.VisitorDataKeys
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.handlers.AccountInfoProvider
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.sdkcore.utils.Completion
import java.util.*

class PrechatExtraData : BotChat() {

    val BOLD_DEPARTMENT = "2278985919139590636"
    val DemoFirstName = "Bold"
    val DemoLastName = "360"

    val accountProvider = MyAccountInfoProvider()

    private val accounts = HashMap<String, AccountInfo>()

    inner class MyAccountInfoProvider : AccountInfoProvider {

        private fun getAccountInfo(apiKey: String): AccountInfo? {
            return accounts[apiKey]
        }

        override fun update(@NonNull account: AccountInfo) {

            val savedAccount = getAccountInfo(account.getApiKey())
            if (savedAccount != null) {
                savedAccount.update(account)

            } else {
                accounts[account.getApiKey()] = account
            }
        }

        override fun provide(account: AccountInfo, callback: Completion<AccountInfo>) {
            var savedAccount: AccountInfo? = getAccountInfo(account.getApiKey())
            if (savedAccount == null) {
                savedAccount = createAccount(account)
                accounts[savedAccount.getApiKey()] = savedAccount
            }
            (savedAccount as? BoldAccount)?.apply {
                addExtraData (
                    VisitorDataKeys.Department to BOLD_DEPARTMENT,
                    VisitorDataKeys.FirstName to DemoFirstName,
                    VisitorDataKeys.LastName to DemoLastName)
            }
            callback.onComplete(savedAccount)
        }

        private fun <T : AccountInfo> createAccount(account: T): T {
            accounts[account.getApiKey()] = account
            return account
        }
    }

    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder().accountProvider( accountProvider )
    }
}
