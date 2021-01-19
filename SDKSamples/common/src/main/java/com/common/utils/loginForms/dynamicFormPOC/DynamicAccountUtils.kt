package com.common.utils.loginForms.dynamicFormPOC

import com.common.utils.loginForms.*
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.bot.BotAccount

fun JsonObject.toBotAccount(): BotAccount {
    return BotAccount(
        get(JsonSharedDataHandler.Access_key)?.asString ?: "",
        get(JsonSharedDataHandler.Account_key)?.asString,
        get(JsonSharedDataHandler.Kb_key)?.asString,
        get(JsonSharedDataHandler.Server_key)?.asString,
        get(JsonSharedDataHandler.Context_key)?.asJsonObject?.let {
            val mapType = object : TypeToken<Map<String, String>>() {}.type
            Gson().fromJson(it, mapType)
        }
    ).apply {
        get(JsonSharedDataHandler.Welcome_key)?.asString
            ?.takeUnless { it.isEmpty() }?.let { welcomeMessage = it }
    }

}

fun JsonObject.toLiveAccount(): BoldAccount {
    return BoldAccount(get(JsonSharedDataHandler.Access_key)?.asString ?: "")
}

fun JsonObject.toAsyncAccount(): AsyncAccount {
    return AsyncAccount(get(JsonSharedDataHandler.Access_key)?.asString ?: "", get(JsonSharedDataHandler.App_id_Key)?.asString ?: "").apply {
            val userId = this@toAsyncAccount.get(JsonSharedDataHandler.user_id_key)?.asString ?: ""
            info.userInfo =
                (userId.takeIf { userId.isNotEmpty() }?.let { UserInfo(userId) }
                    ?: UserInfo()).apply {
                    email =
                        this@toAsyncAccount.get(JsonSharedDataHandler.Email_key)?.asString ?: ""
                    phoneNumber =
                        this@toAsyncAccount.get(JsonSharedDataHandler.Phone_Number_key)?.asString
                            ?: ""
                    firstName =
                        this@toAsyncAccount.get(JsonSharedDataHandler.First_Name_key)?.asString
                            ?: ""
                    lastName =
                        this@toAsyncAccount.get(JsonSharedDataHandler.Last_Name_key)?.asString
                            ?: ""
                    countryAbbrev =
                        this@toAsyncAccount.get(JsonSharedDataHandler.Country_Abbrev_key)?.asString
                            ?: ""
                }
    }
}

fun JsonObject.getGroupId(): String {
    return "${get(JsonSharedDataHandler.Account_key) ?: ""}#${get(JsonSharedDataHandler.Kb_key)}#${
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
                fullInfo.get(JsonSharedDataHandler.Access_key).asString
            )
            fullInfo.get(JsonSharedDataHandler.App_id_Key).asString?.let {
                addProperty(
                    JsonSharedDataHandler.App_id_Key, it
                )
            }
            fullInfo.get(JsonSharedDataHandler.user_id_key)?.asString?.let {
                addProperty(
                    JsonSharedDataHandler.user_id_key, it
                )
            }
            fullInfo.get(JsonSharedDataHandler.Email_key)?.asString?.let {
                addProperty(
                    JsonSharedDataHandler.Email_key, it
                )
            }
            fullInfo.get(JsonSharedDataHandler.Phone_Number_key)?.asString?.let {
                addProperty(
                    JsonSharedDataHandler.Phone_Number_key, it
                )
            }
            fullInfo.get(JsonSharedDataHandler.First_Name_key)?.asString?.let {
                addProperty(
                    JsonSharedDataHandler.First_Name_key, it
                )
            }
            fullInfo.get(JsonSharedDataHandler.Last_Name_key)?.asString?.let {
                addProperty(
                    JsonSharedDataHandler.Last_Name_key, it
                )
            }
            fullInfo.get(JsonSharedDataHandler.Country_Abbrev_key)?.asString?.let {
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
                fullInfo.get(JsonSharedDataHandler.Access_key).asString
            )
        }

    }
}

internal fun JsonObject.toNeededBotInfo(): JsonObject {
    return JsonObject().apply {
        this@toNeededBotInfo.let { fullInfo ->
            addProperty(
                JsonSharedDataHandler.Account_key,
                fullInfo.get(JsonSharedDataHandler.Account_key).asString
            )
            addProperty(
                JsonSharedDataHandler.Kb_key,
                fullInfo.get(JsonSharedDataHandler.Kb_key).asString
            )
            fullInfo.get(JsonSharedDataHandler.Access_key)?.asString?.let {
                addProperty(
                    JsonSharedDataHandler.Access_key, it
                )
            }
            fullInfo.get(JsonSharedDataHandler.Server_key)?.asString?.let {
                addProperty(
                    JsonSharedDataHandler.Server_key, it
                )
            }
            fullInfo.get(JsonSharedDataHandler.Context_key)?.asJsonObject?.let {
                add(
                    JsonSharedDataHandler.Context_key, it
                )
            }
            fullInfo.get(JsonSharedDataHandler.Welcome_key)?.asString?.let {
                addProperty(
                    JsonSharedDataHandler.Welcome_key, it
                )
            }
        }
    }
}