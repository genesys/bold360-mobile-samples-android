package com.sdk.samples.common

import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import java.io.Serializable


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
/*

fun AccountMap.toAccount() : Account {
        return when(SharedDataHandler.ChatType_key) {
                ChatType.AsyncChat -> toAsyncAccount()
                ChatType.LiveChat -> toLiveAccount()
                else -> toBotAccount()
        }
}
*/

fun Serializable.toAccount(@ChatType chatType: String) : Account? {

        return when (chatType) {
                ChatType.AsyncChat -> toAsyncAccount()
                ChatType.LiveChat -> toLiveAccount()
                else -> toBotAccount()
        }
}


private fun Serializable.toBotAccount(): BotAccount? {
        return (this as? Map<String, Any>?)?.let {
                BotAccount(
                        it[BotSharedDataHandler.ApiKey_key] as String,
                        it[BotSharedDataHandler.Account_key] as String,
                        it[BotSharedDataHandler.Kb_key] as String,
                        it[BotSharedDataHandler.Server_key] as String,
                        it[BotSharedDataHandler.Context_key] as Map<String, String>?)
        }
}

private fun Serializable.toLiveAccount(): BoldAccount? {
        return (this as? Map<String, String>)?.let {
                BoldAccount(it[LiveSharedDataHandler.Access_key] as String)
        }
}
private fun Serializable.toAsyncAccount(): AsyncAccount? {
        return (this as? Map<String, String>)?.let {
                AsyncAccount(it[AsyncSharedDataHandler.Access_key] as String)
        }
}
/*

private fun AccountMap.toBotAccount(): BotAccount {
        return BotAccount(
                get(BotSharedDataHandler.ApiKey_key) as String,
                get(BotSharedDataHandler.Account_key) as String,
                get(BotSharedDataHandler.Kb_key) as String,
                get(BotSharedDataHandler.Server_key) as String,
                get(BotSharedDataHandler.Context_key) as? Map<String, String>?)
}

private fun AccountMap.toLiveAccount(): BoldAccount {
        return BoldAccount(get(LiveSharedDataHandler.Access_key) as String)
}
private fun AccountMap.toAsyncAccount(): AsyncAccount {
        return AsyncAccount(get(AsyncSharedDataHandler.Access_key) as String)
}*/
