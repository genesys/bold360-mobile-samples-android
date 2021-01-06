package com.common.utils.loginForms

import android.content.Context
import com.common.utils.loginForms.accountUtils.*
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount

interface RestoreStateProvider {

    /**
     * @param restorable is true if the restoration is possible for the account
     */
    var restorable: Boolean


    /**
     * @param isRestore is true if the user requested to restore the chat
     */
    var restoreRequest : Boolean
}

interface DataController: AccountListener, RestoreStateProvider {

    /**
     * true is the user pressed on the restore button
     */
    var chatType: String

    /**
     * Contains the wanted extra params of the account
     */
    var extraParams: List<String>?

    /**
     * Being called when the AccountForm had been submitted
     */
    fun onSubmit(account: Account?)

    /**
     * Gets the prev account data from the shared properties (according to the ChatType), If null it returns the default account
     */
    fun getAccount(context: Context?): Account

    /**
     * If changed, updates the shared properties to include the updated account details
     */
    fun updateAccount(context: Context?, account: Account, extraData: Map<String, Any?>? = null)
}

class SharedDataController: DataController, RestoreStateProvider {

    override var restorable: Boolean = false
    override var restoreRequest: Boolean = false
    var extraData: Map<String, Any?>? = null

    override var onAccountData: ((account: Account?, restoreState: RestoreState, extraData: Map<String, Any?>?) -> Unit?)? = null

    override fun onSubmit(account: Account?) {
        onAccountData?.invoke(account, RestoreState(restoreRequest, restorable), extraData)
    }

    override var extraParams: List<String>? = null

    override var chatType: String = ChatType.None
    set(value) {
        field = value
        sharedDataHandler =  when (chatType) {
            ChatType.Live -> LiveSharedDataHandler()
            ChatType.Async -> AsyncSharedDataHandler()
            else -> BotSharedDataHandler()
        }
    }

    private var sharedDataHandler: SharedDataHandler? = null

    override fun getAccount(context: Context?): Account {
        return ( context?.let { sharedDataHandler?.getAccount(it) }.orDefault(chatType) )
    }

    override fun updateAccount(context: Context?, account: Account, extraData: Map<String, Any?>?) {

        restorable = account.isRestorable(getAccount(context))
        this.extraData = extraData

        context?.let { sharedDataHandler?.saveAccount(it, account) }
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

    abstract fun getAccount(context: Context): Account?

    protected fun saveData(context: Context, sharedName: String, data: Map<String, Any?>) {
        val shared = context.getSharedPreferences(sharedName, 0)
        val editor = shared.edit()
        data.forEach { detail ->
            (detail.value as? String)?.run { editor.putString(detail.key, this) }
            (detail.value as? Set<String>)?.run { editor.putStringSet(detail.key, this) }
        }
        editor.apply() //commit()
    }

    abstract fun saveAccount(context: Context, data: Account?)

}

class BotSharedDataHandler: SharedDataHandler() {

    companion object {
        const val SharedName = "ChatDataPref.bot"
        const val Account_key = "accountKey"
        const val Kb_key = "kbKey"
        const val Server_key = "serverKey"
        const val Context_key = "contextKey"
        const val Welcome_key = "welcomeKey"
        const val ApiKey_key = "apiKey"
        const val preChat_fName_key = "preFname"
        const val preChat_lName_key = "preLname"
        const val preChat_deptCode_key = "preDeptCode"
    }

    override val chatType: String
        get() = ChatType.Bot

    override fun getAccount(context: Context): BotAccount? {
        val shared = context.getSharedPreferences(SharedName, 0)
        return mapOf(
            ChatType_key to chatType,
            Account_key to shared.getString(Account_key, "nanorep"),
            Kb_key to shared.getString(Kb_key, "English"),
            Server_key to shared.getString(Server_key, ""),
            ApiKey_key to shared.getString(ApiKey_key, "cfeb2b63-785d-48dd-8546-b5444e25a63d"),
            Context_key to shared.getStringSet(Context_key, mutableSetOf())
        ).toBotAccount()
    }

    override fun saveAccount(context: Context, data: Account?) {
        saveData(context, SharedName, (data as? BotAccount)?.map() ?: mapOf())
    }
}

internal class AsyncSharedDataHandler: SharedDataHandler() {

    companion object {
        const val SharedName = "ChatDataPref.async"
        const val Access_key = "accessKey"
        const val App_id_Key = "appIdKey"
        const val First_Name_key = "firstName"
        const val Last_Name_key = "lastName"
        const val Country_Abbrev_key = "countryAbbrev"
        const val Email_key = "email"
        const val Phone_Number_key = "phoneNumber"
        const val user_id_key = "userIdKey"

    }

    override val chatType: String
        get() = ChatType.Async

    override fun getAccount(context: Context): AsyncAccount? {
        val shared = context.getSharedPreferences(SharedName, 0)
        return mapOf(
            ChatType_key to chatType,
            Access_key to shared.getString(Access_key, "2307475884:2403340045369405:KCxHNTjbS7qDY3CVmg0Z5jqHIIceg85X:alphawd2"),
            First_Name_key to shared.getString(First_Name_key, ""),
            Last_Name_key to shared.getString(Last_Name_key, ""),
            Country_Abbrev_key to shared.getString(Country_Abbrev_key, ""),
            Email_key to shared.getString(Email_key, ""),
            Phone_Number_key to shared.getString(Phone_Number_key, ""),
            user_id_key to shared.getString(user_id_key, ""),
            App_id_Key to shared.getString(App_id_Key, "")
        ).toAsyncAccount()
    }

    override fun saveAccount(context: Context, data: Account?) {
        saveData(context, SharedName, (data as? AsyncAccount)?.map() ?: mapOf())
    }
}

internal class LiveSharedDataHandler: SharedDataHandler() {

    companion object {
        const val SharedName = "ChatDataPref.bold"
        const val Access_key = "accessKey"
    }

    override val chatType: String
        get() = ChatType.Live

    override fun getAccount(context: Context): BoldAccount? {
        val shared = context.getSharedPreferences(SharedName, 0)
        return mapOf(
            ChatType_key to chatType,
            Access_key to shared.getString(Access_key, "2300000001700000000:2279145895771367548:MGfXyj9naYgPjOZBruFSykZjIRPzT1jl")
        ).toLiveAccount()
    }

    override fun saveAccount(context: Context, data: Account?) {
        saveData(context, SharedName, (data as? BoldAccount)?.map() ?: mapOf())
    }
}
