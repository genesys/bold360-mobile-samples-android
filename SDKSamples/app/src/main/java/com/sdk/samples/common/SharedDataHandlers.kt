package com.sdk.samples.common

import android.content.Context
import com.sdk.samples.topics.Accounts

interface DataController: AccountListener {

    /**
     * true is the user pressed on the restore button
     */
    var chatType: String?


    /**
     * true is the user pressed on the restore button at the ChatRestore sample
     */
    var isRestore: Boolean

    /**
     * Being called when the AccountForm had been submitted
     */
    fun onSubmit(account: Map<String, Any?>)

    /**
     * Gets the prev account data from the shared properties (according to the ChatType), If null it returns the default account
     */
    fun getAccount(context: Context?): Map<String, Any?>

    /**
     * If changed, updates the shared properties to include the updated account details
     */
    fun updateAccount(context: Context?, account: Map<String, Any?>)
}

class SharedDataController: DataController {

    override var isRestore: Boolean = false
    var currentAccount: Map<String, Any?>? = null

    override var onAccountData: ((account: Map<String, Any?>?, isRestore: Boolean) -> Unit)? = null

    override fun onSubmit(account: Map<String, Any?>) {
        onAccountData?.invoke(account, isRestore)
    }

    override var chatType: String? = null
    set(value) {
        field = value
        sharedDataHandler =  when (chatType) {
            ChatType.LiveChat -> LiveSharedDataHandler()
            ChatType.AsyncChat -> AsyncSharedDataHandler()
            else -> BotSharedDataHandler()
        }
    }

    private var sharedDataHandler: SharedDataHandler? = null

    override fun getAccount(context: Context?): Map<String, Any?> {
        return ( context?.let { sharedDataHandler?.getAccountData(it) } ?: getDefaultAccount() ).also { accountData ->
            currentAccount = accountData
        }
    }

    private fun getDefaultAccount(): Map<String, Any?> {
        return when (chatType) {
                ChatType.LiveChat -> Accounts.defaultBoldAccount.map()
                ChatType.AsyncChat -> Accounts.defaultAsyncAccount.map()
                else -> Accounts.defaultBotAccount.map()
        }
    }

    override fun updateAccount(context: Context?, account: Map<String, Any?>) {
        currentAccount?.let { if (account.dataEqualsTo(it)) return}
        context?.let { sharedDataHandler?.saveChatData(it, account) }
    }
}

/**
 * Handles the shared preference interaction to save and retrieve last applied data to forms.
 */
abstract class SharedDataHandler {

    companion object {
        const val ChatType_key = "chatType"
    }

    @ChatType
    abstract val chatType: String

    abstract fun getAccountData(context: Context): Map<String, Any?>

    protected fun saveData(context: Context, sharedName: String, data: Map<String, Any?>) {
        val shared = context.getSharedPreferences(sharedName, 0)
        val editor = shared.edit()
        data.forEach { detail ->
            (detail.value as? String)?.run { editor.putString(detail.key, this) }
            (detail.value as? Set<String>)?.run { editor.putStringSet(detail.key, this) }
        }
        editor.apply() //commit()
    }

    abstract fun saveChatData(context: Context, data: Map<String, Any?>?)

}

internal class BotSharedDataHandler: SharedDataHandler() {

    companion object {
        const val SharedName = "ChatDataPref.bot"
        const val Account_key = "accountKey"
        const val Kb_key = "kbKey"
        const val Server_key = "serverKey"
        const val Context_key = "contextKey"
        const val Welcome_key = "welcomeKey"
        const val ApiKey_key = "apiKey"

    }

    override val chatType: String
        get() = ChatType.BotChat

    override fun getAccountData(context: Context): Map<String, Any?> {
        val shared = context.getSharedPreferences(SharedName, 0)
        return mapOf(
            ChatType_key to chatType,
            Account_key to shared.getString(Account_key, "nanorep"),
            Kb_key to shared.getString(Kb_key, "English"),
            Server_key to shared.getString(Server_key, ""),
            ApiKey_key to shared.getString(ApiKey_key, "cfeb2b63-785d-48dd-8546-b5444e25a63d"),
            Context_key to shared.getStringSet(Context_key, mutableSetOf())
        )
    }

    override fun saveChatData(context: Context, data: Map<String, Any?>?) {
        saveData(context, SharedName, data ?: mapOf())
    }
}

internal class AsyncSharedDataHandler: SharedDataHandler() {

    companion object {
        const val SharedName = "ChatDataPref.async"
        const val Access_key = "accessKey"
        const val First_Name_key = "firstName"
        const val Last_Name_key = "lastName"
        const val Email_key = "email"
        const val Phone_Number_key = "phoneNumber"
        const val user_id_key = "userId"

    }

    override val chatType: String
        get() = ChatType.AsyncChat

    override fun getAccountData(context: Context): Map<String, Any?> {
        val shared = context.getSharedPreferences(SharedName, 0)
        return mapOf(
            ChatType_key to chatType,
            Access_key to shared.getString(Access_key, "2307475884:2403340045369405:KCxHNTjbS7qDY3CVmg0Z5jqHIIceg85X:alphawd2"),
            First_Name_key to shared.getString(First_Name_key, ""),
            Last_Name_key to shared.getString(Last_Name_key, ""),
            Email_key to shared.getString(Email_key, ""),
            Phone_Number_key to shared.getString(Phone_Number_key, ""),
            user_id_key to shared.getString(user_id_key, "")
        )
    }

    override fun saveChatData(context: Context, data: Map<String, Any?>?) {
        saveData(context, SharedName, data ?: mapOf())
    }
}

internal class LiveSharedDataHandler: SharedDataHandler() {

    companion object {
        const val SharedName = "ChatDataPref.bold"
        const val Access_key = "accessKey"
    }

    override val chatType: String
        get() = ChatType.LiveChat

    override fun getAccountData(context: Context): Map<String, Any?> {
        val shared = context.getSharedPreferences(SharedName, 0)
        return mapOf(
            ChatType_key to chatType,
            Access_key to shared.getString(Access_key, "2300000001700000000:2279145895771367548:MGfXyj9naYgPjOZBruFSykZjIRPzT1jl"))
    }

    override fun saveChatData(context: Context, data: Map<String, Any?>?) {
        saveData(context, SharedName, data ?: mapOf())
    }
}
