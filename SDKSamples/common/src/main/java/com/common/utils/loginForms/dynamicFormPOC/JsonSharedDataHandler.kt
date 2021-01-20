package com.common.utils.loginForms.dynamicFormPOC

import android.content.Context
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.google.gson.Gson
import com.google.gson.JsonObject

interface SharedDataHandler {

    /**
     * Gets the prev account data from the shared properties, If null it returns the default account
     */
    fun getSavedAccount(context: Context, @ChatType chatType: String): Any?

    /**
     * If changed, updates the shared properties to include the updated account details
     */
    fun saveAccount(context: Context, accountData: Any?, @ChatType chatType: String)

    /**
     * Checks if the account is restorable
     */
    fun isRestorable(context: Context, @ChatType chatType: String): Boolean

    companion object {
        const val Access_key = "accessKey"
        const val Account_key = "account"
        const val Kb_key = "kb"
        const val Server_key = "domain"
        const val Context_key = "contextKey"
        const val Welcome_key = "welcomeKey"
        const val ApiKey_key = "apiKey"
        const val preChat_fName_key = "preFname"
        const val preChat_lName_key = "preLname"
        const val preChat_deptCode_key = "preDeptCode"
        const val App_id_Key = "appIdKey"
        const val First_Name_key = "firstName"
        const val Last_Name_key = "lastName"
        const val Country_Abbrev_key = "countryAbbrev"
        const val Email_key = "email"
        const val Phone_Number_key = "phoneNumber"
        const val user_id_key = "userIdKey"
    }
}

class JsonSharedDataHandler: SharedDataHandler {

    private fun getSaved(context: Context, @ChatType chatType: String) : JsonObject? {
        return context.getSharedPreferences("accounts", 0).getString(chatType, null)?.let { Gson().fromJson(it, JsonObject::class.java) }
    }

    override fun getSavedAccount(context: Context, @ChatType chatType: String): JsonObject {
        return getSaved(context, chatType).orDefault(chatType)
    }

    override fun saveAccount(context: Context, accountData: Any?, @ChatType chatType: String) {

        context.getSharedPreferences("accounts", 0).let { shared ->
            val editor = shared.edit()
            editor.putString(
                chatType,
                (accountData as? JsonObject)?.toNeededInfo(chatType).toString()
            )
            editor.apply()
        }
    }

    override fun isRestorable(context: Context, @ChatType chatType: String): Boolean {
        return chatType == ChatType.None || getSaved(context, chatType) != null
    }
}
