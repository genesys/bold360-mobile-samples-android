package com.sdk.samples.common

import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.bot.BotAccount

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

internal fun Map<String,Any?>?.toAccount(@ChatType chatType: String) {

}

fun Pair<String, String>.isEmpty(): Boolean {
    return first.isBlank() || second.isBlank()
}
