package com.common.utils.loginForms.dynamicFormPOC

import com.common.utils.loginForms.*
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.bot.BotAccount

fun JsonObject.toBotAccount(): BotAccount {
    return BotAccount(
        getString(JsonSharedDataHandler.Access_key) ?: "",
        getString(JsonSharedDataHandler.Account_key),
        getString(JsonSharedDataHandler.Kb_key),
        getString(JsonSharedDataHandler.Server_key),
        get(JsonSharedDataHandler.Context_key)?.asJsonObject?.let {
            val mapType = object : TypeToken<Map<String, String>>() {}.type
            Gson().fromJson(it, mapType)
        }
    ).apply {
        getString(JsonSharedDataHandler.Welcome_key)?.takeUnless { it.isEmpty() }?.let { welcomeMessage = it }
    }

}

fun JsonObject.toLiveAccount(): BoldAccount {
    return BoldAccount(getString(JsonSharedDataHandler.Access_key) ?: "")
}

fun JsonObject.toAsyncAccount(): AsyncAccount {
    return AsyncAccount(getString(JsonSharedDataHandler.Access_key) ?: "", getString(JsonSharedDataHandler.App_id_Key) ?: "").apply {
            val userId = this@toAsyncAccount.getString(JsonSharedDataHandler.user_id_key) ?: ""
            info.userInfo =
                (userId.takeIf { userId.isNotEmpty() }?.let { UserInfo(userId) }
                    ?: UserInfo()).apply {
                    email =
                        this@toAsyncAccount.getString(JsonSharedDataHandler.Email_key) ?: ""
                    phoneNumber =
                        this@toAsyncAccount.getString(JsonSharedDataHandler.Phone_Number_key) ?: ""
                    firstName =
                        this@toAsyncAccount.getString(JsonSharedDataHandler.First_Name_key) ?: ""
                    lastName =
                        this@toAsyncAccount.getString(JsonSharedDataHandler.Last_Name_key) ?: ""
                    countryAbbrev =
                        this@toAsyncAccount.getString(JsonSharedDataHandler.Country_Abbrev_key) ?: ""
                }
    }
}

fun JsonObject.getGroupId(): String {
    return "${getString(JsonSharedDataHandler.Account_key) ?: ""}#${getString(JsonSharedDataHandler.Kb_key)}#${
        get(
            JsonSharedDataHandler.Access_key
        )
    }"
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
            addProperty(
                JsonSharedDataHandler.Access_key,
                fullInfo.getString(JsonSharedDataHandler.Access_key)
            )
            fullInfo.getString(JsonSharedDataHandler.App_id_Key)?.let {
                addProperty(
                    JsonSharedDataHandler.App_id_Key, it
                )
            }
            fullInfo.getString(JsonSharedDataHandler.user_id_key)?.let {
                addProperty(
                    JsonSharedDataHandler.user_id_key, it
                )
            }
            fullInfo.getString(JsonSharedDataHandler.Email_key)?.let {
                addProperty(
                    JsonSharedDataHandler.Email_key, it
                )
            }
            fullInfo.getString(JsonSharedDataHandler.Phone_Number_key)?.let {
                addProperty(
                    JsonSharedDataHandler.Phone_Number_key, it
                )
            }
            fullInfo.getString(JsonSharedDataHandler.First_Name_key)?.let {
                addProperty(
                    JsonSharedDataHandler.First_Name_key, it
                )
            }
            fullInfo.getString(JsonSharedDataHandler.Last_Name_key)?.let {
                addProperty(
                    JsonSharedDataHandler.Last_Name_key, it
                )
            }
            fullInfo.getString(JsonSharedDataHandler.Country_Abbrev_key)?.let {
                addProperty(
                    JsonSharedDataHandler.user_id_key, it
                )
            }
        }

    }
}

internal fun JsonObject.toNeededLiveInfo(): JsonObject {
    return JsonObject().apply {
        this@toNeededLiveInfo.let { fullInfo ->
            addProperty(
                JsonSharedDataHandler.Access_key,
                fullInfo.getString(JsonSharedDataHandler.Access_key)
            )
        }

    }
}

internal fun JsonObject.toNeededBotInfo(): JsonObject {
    return JsonObject().apply {
        this@toNeededBotInfo.let { fullInfo ->
            addProperty(
                JsonSharedDataHandler.Account_key,
                fullInfo.getString(JsonSharedDataHandler.Account_key)
            )
            addProperty(
                JsonSharedDataHandler.Kb_key,
                fullInfo.getString(JsonSharedDataHandler.Kb_key)
            )
            fullInfo.getString(JsonSharedDataHandler.Access_key)?.let {
                addProperty(
                    JsonSharedDataHandler.Access_key, it
                )
            }
            fullInfo.getString(JsonSharedDataHandler.Server_key)?.let {
                addProperty(
                    JsonSharedDataHandler.Server_key, it
                )
            }
            fullInfo.get(JsonSharedDataHandler.Context_key)?.asJsonObject?.let {
                add(
                    JsonSharedDataHandler.Context_key, it
                )
            }
            fullInfo.getString(JsonSharedDataHandler.Welcome_key)?.let {
                addProperty(
                    JsonSharedDataHandler.Welcome_key, it
                )
            }
        }
    }
}

fun JsonObject.getString(key: String): String? {
    return get(key).asString
}

fun JsonElement.getString(key: String): String? {
    return asJsonObject?.getString(key)
}