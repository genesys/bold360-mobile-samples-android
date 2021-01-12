package com.common.utils.loginForms

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.common.utils.chat.LoginData
import com.common.utils.loginForms.accountUtils.ChatType
import com.common.utils.loginForms.accountUtils.isRestorable
import com.common.utils.loginForms.accountUtils.orDefault
import com.nanorep.nanoengine.Account

class LoginFormViewModel : ViewModel(), DataController {

    val loginData: MutableLiveData<LoginData> = MutableLiveData()

    override var restorable: Boolean = false
    override var restoreRequest: Boolean = false
    override var extraData: Map<String, Any?>? = null

    override var extraParams: List<String>? = null

    override var chatType: String = ChatType.None
        set(value) {
            field = value
            sharedDataHandler = when (chatType) {
                ChatType.Live -> LiveSharedDataHandler()
                ChatType.Async -> AsyncSharedDataHandler()
                ChatType.Bot -> BotSharedDataHandler()
                else -> null
            }
        }

    private var sharedDataHandler: SharedDataHandler? = null

    override fun getAccount(context: Context?): Account? {
        return (context?.let { sharedDataHandler?.getAccount(it) }.orDefault(chatType))
    }

    override fun updateAccount(context: Context?, account: Account, extraData: Map<String, Any?>?) {

        restorable = account.isRestorable(getAccount(context))
        this.extraData = extraData

        context?.let { sharedDataHandler?.saveAccount(it, account) }
    }

    fun onStartChat(account: Account?) {
        loginData.value = object : LoginData {
            override var account = account
            override var extraData = this@LoginFormViewModel.extraData
            override var restoreState = RestoreState(restoreRequest, restorable)
        }
    }
}