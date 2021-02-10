package com.common.utils.chatForm

import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys.*
import com.common.utils.chatForm.defs.FieldProps
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.bot.BotAccount
import kotlinx.android.synthetic.main.account_form.*

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
                    infoJson.getString(Email)?.let { email = it }
                    infoJson.getString(PhoneNumber)?.let { phoneNumber = it }
                    infoJson.getString(FirstName)?.let { firstName = it }
                    infoJson.getString(LastName)?.let { lastName = it }
                    infoJson.getString(CountryAbbrev)?.let { countryAbbrev = it }
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
                    extraData.copySimpleProp(Email, this)
                    extraData.copySimpleProp(PhoneNumber, this)
                    extraData.copySimpleProp(FirstName, this)
                    extraData.copySimpleProp(LastName, this)
                    extraData.copySimpleProp(LastName, this)
                    extraData.copySimpleProp(CountryAbbrev, this)
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
    return key?.let { get(it)?.asString }
}

fun JsonArray.applyValues(accountObject: JsonObject): JsonArray {
    return this.onEach {
        try {
            (it.asJsonObject).let { fieldObject ->
                val key = fieldObject.getString(FieldProps.Key) //-> Gets the key of the specific field data
                accountObject.getString(key)?.let { value -> // -> Gets the value of the same key from the account data
                    fieldObject.addProperty(FieldProps.Value, value)
                }
            }
        } catch (e: IllegalStateException) {

        }
    }
}

fun Pair<String, String>.isEmpty() : Boolean{
    return first.isEmpty() || second.isEmpty()
}
