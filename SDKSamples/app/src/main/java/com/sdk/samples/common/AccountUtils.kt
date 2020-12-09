package com.sdk.samples.common

import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.topics.Accounts


@Override
internal fun BotAccount.map(): Map<String, Any?> =
        mapOf(
                BotSharedDataHandler.Account_key to (account ?: ""),
                BotSharedDataHandler.Kb_key to (knowledgeBase ?: ""),
                BotSharedDataHandler.Server_key to (domain ?: ""),
                BotSharedDataHandler.ApiKey_key to apiKey,
                BotSharedDataHandler.Welcome_key to welcomeMessage,
                BotSharedDataHandler.Context_key to (contexts?.takeIf { it.isNotEmpty() }?.map { entry ->
                    "key= ${entry.key} value= ${entry.value}"
                }?.mapIndexed { index, str -> "$index:$str" }?.toHashSet() ?: setOf<String>()))

@Override
internal fun BoldAccount.map(): Map<String, Any?> =
        mapOf(LiveSharedDataHandler.Access_key to apiKey)

@Override
internal fun AsyncAccount.map(): Map<String, Any?> =
        mapOf(AsyncSharedDataHandler.Access_key to apiKey)

///////////////////////////////////////////////////

fun Pair<String, String>.isEmpty(): Boolean {
    return first.isBlank() || second.isBlank()
}

fun <T: Account> Account?.castedNoneNull(): T? {
        return (when (Class<T>::getName.toString()) {
                "BotAccount" -> this as? BotAccount ?: Accounts.defaultBotAccount
                "AsyncAccount" -> this as? AsyncAccount ?: Accounts.defaultAsyncAccount
                else -> this as? BoldAccount ?: Accounts.defaultBoldAccount
        } as? T )
}


fun Map<String, Any?>.dataEqualsTo(other: Map<String, Any?>): Boolean {

        if (other.size != size) return false

        val otherKeys = other.keys
        val otherValues = other.values

        values.forEachIndexed { index, value ->
                if (value != otherValues.elementAt(index)) return false
        }

        keys.forEachIndexed { index, key ->
                if (key != otherKeys.elementAt(index)) return false
        }

        return true
}

typealias AccountMap = Map<String,Any?>

fun AccountMap.toAccount() : Account? {
        return (this as? AccountMap)?.let { accountMap ->
                when (accountMap[SharedDataHandler.ChatType_key]) {
                        ChatType.AsyncChat -> accountMap.toAsyncAccount()
                        ChatType.LiveChat -> accountMap.toLiveAccount()
                        ChatType.BotChat -> accountMap.toBotAccount()
                        else -> null
                }
        }

}

fun AccountMap.toBotAccount(): BotAccount {
        return BotAccount(
                this[BotSharedDataHandler.ApiKey_key] as String,
                this[BotSharedDataHandler.Account_key] as String,
                this[BotSharedDataHandler.Kb_key] as String,
                this[BotSharedDataHandler.Server_key] as String,
                this[BotSharedDataHandler.Context_key] as? Map<String, String>?).apply {
                        (get(BotSharedDataHandler.Welcome_key) as? String)?.takeUnless { it.isEmpty() }?.let { welcomeMessage = it }
        }

}

fun AccountMap.toLiveAccount(): BoldAccount {
        return BoldAccount(this[LiveSharedDataHandler.Access_key] as String)
}

fun AccountMap.toAsyncAccount(): AsyncAccount {
        return  AsyncAccount(this[AsyncSharedDataHandler.Access_key] as String)
}