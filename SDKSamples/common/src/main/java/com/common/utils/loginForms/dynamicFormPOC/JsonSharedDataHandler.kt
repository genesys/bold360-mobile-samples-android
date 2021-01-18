package com.common.utils.loginForms.dynamicFormPOC

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject

class JsonSharedDataHandler {

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

    fun getAccount(context: Context, accountId: String? = null): JsonObject? {
        val shared = context.getSharedPreferences("accounts", 0)
        return shared.getString(accountId, null)?.let { Gson().fromJson(it, JsonObject::class.java) }
    }

    fun saveAccount(context: Context, accountData: JsonObject, accountId: String) {

        val shared = context.getSharedPreferences("accounts", 0)
        val editor = shared.edit()
        editor.putString(accountId, accountData.toString())
        editor.apply()
    }

    fun isRestorable(context: Context, accountId: String?): Boolean {
        return accountId?.let { getAccount(context, it) }?.let { true } ?: false
    }

}
