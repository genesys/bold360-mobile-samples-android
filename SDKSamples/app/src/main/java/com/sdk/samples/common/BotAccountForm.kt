package com.sdk.samples.common

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.R
import com.sdk.samples.topics.Accounts

class BotAccountForm(dataHandler: AccountDataHandler) : AccountFragment(dataHandler) {

    override val formLayoutRes: Int
        get() = R.layout.frgment_bot_form

    override fun fillFields() {
        val account = dataHandler.getAccount() as? BotAccount
    }

    override fun validateFormData(): Account? {
        return Accounts.defaultBotAccount
    }

    companion object {
        fun newInstance(dataHandler: AccountDataHandler): BotAccountForm {
            return BotAccountForm(dataHandler)
        }
    }
}