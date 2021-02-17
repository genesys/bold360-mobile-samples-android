package com.common.utils.chatForm

import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys.*
import com.common.utils.chatForm.defs.FieldProps
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.nanoengine.model.conversation.SessionInfoKeys
import kotlinx.android.synthetic.main.chat_form.*

fun JsonObject.toBotAccount(): BotAccount {
    return BotAccount(
        getString(Accesskey).orEmpty(),
        getString(AccountName),
        getString(KB),
        getString(Server)
    ).apply {

        getString(Context)?.let {
            contexts = Gson().fromJson<Map<String, String>>(it, Map::class.java)
        }

        getString(Welcome)?.takeUnless { it.isEmpty() }?.let { welcomeMessage = it }
    }

}

fun JsonObject.toLiveAccount(): BoldAccount {
    return BoldAccount(getString(Accesskey).orEmpty())
}

fun JsonObject.toAsyncAccount(): AsyncAccount {

    val infoJson = getAsJsonObject(Info)
    return AsyncAccount(getString(Accesskey).orEmpty(), infoJson?.getString(AppId).orEmpty()).apply {
        info.userInfo =
            (infoJson?.getString(UserId)?.takeIf { it.isNotEmpty() }?.let { UserInfo(it) } ?: UserInfo()).apply {
                infoJson?.let { infoJson ->
                    infoJson.getString(SessionInfoKeys.Email)?.let { email = it }
                    infoJson.getString(SessionInfoKeys.Phone)?.let { phoneNumber = it }
                    infoJson.getString(SessionInfoKeys.FirstName)?.let { firstName = it }
                    infoJson.getString(SessionInfoKeys.LastName)?.let { lastName = it }
                    infoJson.getString(SessionInfoKeys.countryAbbrev)?.let { countryAbbrev = it }
                }
            }
    }
}

internal fun JsonObject?.orDefault(@ChatType chatType: String): JsonObject {
    return this ?: Gson().fromJson(
        when (chatType) {
            ChatType.Live -> Gson().toJson(Accounts.defaultBoldAccount)
            ChatType.Async -> Gson().toJson(Accounts.defaultAsyncAccount)
            else -> Gson().toJson(Accounts.defaultBotAccount)
        }, JsonObject::class.java
    ).toNeededInfo(chatType)
}

internal fun JsonObject.toNeededInfo(@ChatType chatType: String): JsonObject {
    return when (chatType) {
        ChatType.Live -> toNeededLiveInfo()
        ChatType.Async -> toNeededAsyncInfo()
        else -> toNeededBotInfo()
    }
}

internal fun JsonObject.toNeededAsyncInfo(): JsonObject {
    return JsonObject().apply {
        this@toNeededAsyncInfo.let { fullInfo ->

            fullInfo.copySimpleProp(Accesskey, this)

            fullInfo.getAsJsonObject(Info).let { info ->
                info.copySimpleProp(UserId, this)

                info.getAsJsonObject("configurations").copySimpleProp(AppId, this)

                info.getAsJsonObject("extraData")?.let { extraData ->
                    extraData.copySimpleProp(SessionInfoKeys.Email, this)
                    extraData.copySimpleProp(SessionInfoKeys.Phone, this)
                    extraData.copySimpleProp(SessionInfoKeys.FirstName, this)
                    extraData.copySimpleProp(SessionInfoKeys.LastName, this)
                    extraData.copySimpleProp(SessionInfoKeys.countryAbbrev, this)
                }
            }
        }

    }
}

internal fun JsonObject.toNeededLiveInfo(): JsonObject {
    return JsonObject().apply {
        this@toNeededLiveInfo.copySimpleProp(Accesskey, this)
    }
}

internal fun JsonObject.toNeededBotInfo(): JsonObject {
    return JsonObject().apply {
        this@toNeededBotInfo.let { fullInfo ->
            fullInfo.copySimpleProp(AccountName, this)
            fullInfo.copySimpleProp(KB, this)
            fullInfo.copySimpleProp(Accesskey, this)
            fullInfo.copySimpleProp(Server, this)
        }
    }
}

fun JsonObject.copySimpleProp(key: String?, other: JsonObject) {
    getString(key)?.let { other.addProperty(key, it) }
}

fun JsonObject.getString(key: String?): String? {
    return try {
        key?.let { get(it)?.asString }
    } catch ( exception : IllegalStateException) {
        // being thrown by the 'JsonElement' casting
        Log.w(ChatForm.TAG, exception.message ?: "Unable to parse field")
        null
    }
}

fun JsonArray.applyValues(accountObject: JsonObject?): JsonArray {
    return this.apply {
        accountObject?.let { accountObject ->
            onEach {
                it.toObject()?.let { fieldObject ->
                    val key = fieldObject.getString(FieldProps.Key) // -> Gets the key of the specific field data

                    accountObject.getString(key)?.let { value ->
                        fieldObject.addProperty(FieldProps.Value, value) // -> Gets the value of the same key from the account data
                    }
                }
            }
        }
    }
}

fun Pair<String, String>.isEmpty() : Boolean{
    return first.isEmpty() || second.isEmpty()
}

fun JsonElement.toObject(catchEmpty: Boolean = false): JsonObject? {
    return try {
        this.asJsonObject
    } catch ( exception : IllegalStateException) {
        // being thrown by the 'JsonElement' casting
        Log.w(ChatForm.TAG, exception.message ?: "Unable to parse field")
        if (catchEmpty) JsonObject() else null
    }
}

fun RadioGroup.getSelectedText() : String? = findViewById<RadioButton>(this.checkedRadioButtonId).text?.toString()
