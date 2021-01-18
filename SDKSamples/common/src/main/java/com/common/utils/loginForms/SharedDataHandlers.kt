package com.common.utils.loginForms

import android.content.Context
import com.common.utils.loginForms.accountUtils.*
import com.google.gson.JsonObject

interface RestoreStateProvider {

    /**
     * @param restorable is true if the restoration is possible for the account
     */
    var restorable: Boolean


    /**
     * @param isRestore is true if the user requested to restore the chat
     */
    var restoreRequest: Boolean
}

interface DataController : RestoreStateProvider {

    /**
     * Extra data relates the account
     */
    var extraData: Map<String, Any?>?

    /**
     * true is the user pressed on the restore button
     */
    var chatType: String

    /**
     * Contains the wanted extra params of the account
     */
    var formsParams: Int

    /**
     * Gets the prev account data from the shared properties (according to the ChatType), If null it returns the default account
     */
    fun getJsonAccount(context: Context? = null): JsonObject?

    /**
     * If changed, updates the shared properties to include the updated account details
     */
    fun updateGenericAccount(context: Context?, accountData: JsonObject, extraData: Map<String, Any?>?)
}

///**
// * Handles the shared preference interaction to save and retrieve last applied data to forms.
// */
//
//abstract class SharedDataHandler {
//
//    @ChatType
//    abstract val chatType: String
//
//    abstract fun getAccount(context: Context): Map<String, Any?>?
//
//    protected fun saveData(context: Context, sharedName: String, data: Map<String, Any?>) {
//        val shared = context.getSharedPreferences(sharedName, 0)
//        val editor = shared.edit()
//        data.forEach { detail ->
//            (detail.value as? String)?.run { editor.putString(detail.key, this) }
//            (detail.value as? Set<String>)?.run { editor.putStringSet(detail.key, this) }
//        }
//        editor.apply() //commit()
//    }
//
//    abstract fun saveAccount(context: Context, data: Account?)
//
//}
//
//class BotSharedDataHandler : SharedDataHandler() {
//
//    companion object {
//        const val SharedName = "ChatDataPref.bot"
//        const val Account_key = "account"
//        const val Kb_key = "kb"
//        const val Server_key = "domain"
//        const val Context_key = "contextKey"
//        const val Welcome_key = "welcomeKey"
//        const val ApiKey_key = "apiKey"
//        const val preChat_fName_key = "preFname"
//        const val preChat_lName_key = "preLname"
//        const val preChat_deptCode_key = "preDeptCode"
//    }
//
//    override val chatType: String
//        get() = ChatType.Bot
//
//    override fun getAccount(context: Context): Map<String, Any?> {
//        val shared = context.getSharedPreferences(SharedName, 0)
//        return mapOf(
//            Account_key to shared.getString(Account_key, "nanorep"),
//            Kb_key to shared.getString(Kb_key, "English"),
//            Server_key to shared.getString(Server_key, ""),
//            Access_key to shared.getString(Access_key, "cfeb2b63-785d-48dd-8546-b5444e25a63d"),
//            Context_key to shared.getStringSet(Context_key, mutableSetOf())
//        )
//    }
//
//    override fun saveAccount(context: Context, data: Account?) {
//        saveData(context, SharedName, (data as? BotAccount)?.map() ?: mapOf())
//    }
//}
//
//internal class AsyncSharedDataHandler : SharedDataHandler() {
//
//    companion object {
//        const val SharedName = "ChatDataPref.async"
//        const val Access_key = "accessKey"
//        const val App_id_Key = "appIdKey"
//        const val First_Name_key = "firstName"
//        const val Last_Name_key = "lastName"
//        const val Country_Abbrev_key = "countryAbbrev"
//        const val Email_key = "email"
//        const val Phone_Number_key = "phoneNumber"
//        const val user_id_key = "userIdKey"
//
//    }
//
//    override val chatType: String
//        get() = ChatType.Async
//
//    override fun getAccount(context: Context): Map<String, Any?> {
//        val shared = context.getSharedPreferences(SharedName, 0)
//        return mapOf(
//            Access_key to shared.getString(
//                Access_key,
//                "2307475884:2403340045369405:KCxHNTjbS7qDY3CVmg0Z5jqHIIceg85X:alphawd2"
//            ),
//            First_Name_key to shared.getString(First_Name_key, ""),
//            Last_Name_key to shared.getString(Last_Name_key, ""),
//            Country_Abbrev_key to shared.getString(Country_Abbrev_key, ""),
//            Email_key to shared.getString(Email_key, ""),
//            Phone_Number_key to shared.getString(Phone_Number_key, ""),
//            user_id_key to shared.getString(user_id_key, ""),
//            App_id_Key to shared.getString(App_id_Key, "")
//        )
//    }
//
//    override fun saveAccount(context: Context, data: Account?) {
//        saveData(context, SharedName, (data as? AsyncAccount)?.map() ?: mapOf())
//    }
//}
//
//internal class LiveSharedDataHandler : SharedDataHandler() {
//
//    companion object {
//        const val SharedName = "ChatDataPref.bold"
//        const val Access_key = "accessKey"
//    }
//
//    override val chatType: String
//        get() = ChatType.Live
//
//    override fun getAccount(context: Context) : Map<String, Any?> {
//        val shared = context.getSharedPreferences(SharedName, 0)
//        return mapOf(
//            Access_key to shared.getString(
//                Access_key,
//                "2300000001700000000:2279145895771367548:MGfXyj9naYgPjOZBruFSykZjIRPzT1jl"
//            )
//        )
//    }
//
//    override fun saveAccount(context: Context, data: Account?) {
//        saveData(context, SharedName, (data as? BoldAccount)?.map() ?: mapOf())
//    }
//}
