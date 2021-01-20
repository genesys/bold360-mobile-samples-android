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
        getString(SharedDataHandler.Access_key).orEmpty(),
        getString(SharedDataHandler.Account_key),
        getString(SharedDataHandler.Kb_key),
        getString(SharedDataHandler.Server_key),
        get(SharedDataHandler.Context_key)?.asJsonObject?.let {
            val mapType = object : TypeToken<Map<String, String>>() {}.type
            Gson().fromJson(it, mapType)
        }
    ).apply {
        getString(SharedDataHandler.Welcome_key)?.takeUnless { it.isEmpty() }?.let { welcomeMessage = it }
    }

}

fun JsonObject.toLiveAccount(): BoldAccount {
    return BoldAccount(getString(SharedDataHandler.Access_key).orEmpty())
}

fun JsonObject.toAsyncAccount(): AsyncAccount {
    return AsyncAccount(getString(SharedDataHandler.Access_key).orEmpty(), getString(SharedDataHandler.App_id_Key).orEmpty()).apply {
            info.userInfo =
                (this@toAsyncAccount.getString(SharedDataHandler.user_id_key)?.takeIf { it.isNotEmpty() }?.let { UserInfo(it) } ?: UserInfo()).apply {
                    this@toAsyncAccount.getString(SharedDataHandler.Email_key)?.let { email = it }
                    this@toAsyncAccount.getString(SharedDataHandler.Phone_Number_key)?.let { phoneNumber = it }
                    this@toAsyncAccount.getString(SharedDataHandler.First_Name_key)?.let { firstName = it }
                    this@toAsyncAccount.getString(SharedDataHandler.Last_Name_key)?.let { lastName = it }
                    this@toAsyncAccount.getString(SharedDataHandler.Country_Abbrev_key)?.let { countryAbbrev = it }
                }
    }
}

fun JsonObject.getGroupId(): String {
    return "${getString(SharedDataHandler.Account_key).orEmpty()}#${getString(SharedDataHandler.Kb_key)}#${
        get(
            SharedDataHandler.Access_key
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
                SharedDataHandler.Access_key,
                fullInfo.getString(SharedDataHandler.Access_key)
            )
            fullInfo.getString(SharedDataHandler.App_id_Key)?.let {
                addProperty(
                    SharedDataHandler.App_id_Key, it
                )
            }
            fullInfo.getString(SharedDataHandler.user_id_key)?.let {
                addProperty(
                    SharedDataHandler.user_id_key, it
                )
            }
            fullInfo.getString(SharedDataHandler.Email_key)?.let {
                addProperty(
                    SharedDataHandler.Email_key, it
                )
            }
            fullInfo.getString(SharedDataHandler.Phone_Number_key)?.let {
                addProperty(
                    SharedDataHandler.Phone_Number_key, it
                )
            }
            fullInfo.getString(SharedDataHandler.First_Name_key)?.let {
                addProperty(
                    SharedDataHandler.First_Name_key, it
                )
            }
            fullInfo.getString(SharedDataHandler.Last_Name_key)?.let {
                addProperty(
                    SharedDataHandler.Last_Name_key, it
                )
            }
            fullInfo.getString(SharedDataHandler.Country_Abbrev_key)?.let {
                addProperty(
                    SharedDataHandler.user_id_key, it
                )
            }
        }

    }
}

internal fun JsonObject.toNeededLiveInfo(): JsonObject {
    return JsonObject().apply {
        addProperty(
            SharedDataHandler.Access_key,
            this@toNeededLiveInfo.getString(SharedDataHandler.Access_key)
        )

    }
}

internal fun JsonObject.toNeededBotInfo(): JsonObject {
    return JsonObject().apply {
        this@toNeededBotInfo.let { fullInfo ->
            addProperty(
                SharedDataHandler.Account_key,
                fullInfo.getString(SharedDataHandler.Account_key)
            )
            addProperty(
                SharedDataHandler.Kb_key,
                fullInfo.getString(SharedDataHandler.Kb_key)
            )
            fullInfo.getString(SharedDataHandler.Access_key)?.let {
                addProperty(
                    SharedDataHandler.Access_key, it
                )
            }
            fullInfo.getString(SharedDataHandler.Server_key)?.let {
                addProperty(
                    SharedDataHandler.Server_key, it
                )
            }
            fullInfo.get(SharedDataHandler.Context_key)?.asJsonObject?.let {
                add(
                    SharedDataHandler.Context_key, it
                )
            }
            fullInfo.getString(SharedDataHandler.Welcome_key)?.let {
                addProperty(
                    SharedDataHandler.Welcome_key, it
                )
            }
        }
    }
}

fun JsonObject.getString(key: String?): String? {
    return key?.let { get(it)?.asString }
}

fun JsonObject.copyTo(key: String?, other: JsonObject) {
    key?.let { get(it)?.asString?.let { value -> other.addProperty(key, value) } }
}