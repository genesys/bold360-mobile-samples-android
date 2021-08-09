package com.common.utils.chatForm

import android.content.Context
import com.common.utils.chatForm.defs.ChatType
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nanorep.sdkcore.utils.weakRef
import java.lang.ref.WeakReference

/**
 * A repository that in charge of the chat continuity data
 */
interface ContinuityRepository {
    /**
     * Saves the VisitorToken to the shared properties
     */
    fun saveVisitorToken( key: String, visitorToken: String? )

    /**
     * Gets a saved VisitorToken from the shared properties
     */
    fun getVisitorToken( key: String ) : String?
}

interface SampleRepository {

    /**
     * Gets the prev account data from the shared properties, If null it returns the default account
     * @param chatType Is being used as the saved account's key
     */
    fun getSavedAccount( @ChatType chatType: String): Any?

    /**
     * If changed, updates the shared properties to include the updated account details
     * @param chatType Is being used as the saved account's key
     */
    fun saveAccount( accountData: Any?, @ChatType chatType: String)

    /**
     * Checks if the account is restorable
     * @param chatType Is being used as the saved account's key
     * @return true if the account found
     */
    fun isRestorable( @ChatType chatType: String): Boolean


    val continuityRepository: ContinuityRepository

}

class JsonSampleRepository( context: Context ): SampleRepository {

    private val wContext: WeakReference<Context> = context.weakRef()

    override val continuityRepository: ContinuityRepository

        get() = object : ContinuityRepository{

            override fun saveVisitorToken( key: String, visitorToken: String?) {
                wContext.get()?.getSharedPreferences("bot_chat_session", 0)?.let { shared ->
                    val editor = shared.edit()
                    editor.putString(
                        key, visitorToken.toString()
                    )
                    editor.apply()
                }
            }

            override fun getVisitorToken( key: String): String? {
                return wContext.get()?.getSharedPreferences("bot_chat_session", 0)?.getString(key, null)
            }
        }

    private fun getSaved( @ChatType chatType: String) : JsonObject? {
        return wContext.get()?.getSharedPreferences("accounts", 0)?.getString(chatType, null)
            ?.let { Gson().fromJson(it, JsonObject::class.java) }
    }

    override fun getSavedAccount( @ChatType chatType: String): JsonObject {
        return chatType.takeIf { it != ChatType.ChatSelection }
            ?.let { getSaved( it ).orDefault(chatType) } ?: JsonObject()
    }

    override fun saveAccount( accountData: Any?, @ChatType chatType: String) {

        wContext.get()?.getSharedPreferences("accounts", 0)?.let { shared ->
            val editor = shared.edit()
            editor.putString(
                chatType,
                (accountData as? JsonObject).toString()
            )
            editor.apply()
        }
    }

    override fun isRestorable( @ChatType chatType: String): Boolean {
        return chatType == ChatType.ContinueLast || getSaved( chatType) != null
    }
}
