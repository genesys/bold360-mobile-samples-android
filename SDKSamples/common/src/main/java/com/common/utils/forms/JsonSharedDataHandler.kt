package com.common.utils.forms

import android.content.Context
import com.common.utils.forms.defs.ChatType
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
                (accountData as? JsonObject).toString()
            )
            editor.apply()
        }
    }

    override fun isRestorable(context: Context, @ChatType chatType: String): Boolean {
        return chatType == ChatType.None || getSaved(context, chatType) != null
    }
}
