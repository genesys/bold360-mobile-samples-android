package com.sdk.samples.common

import com.integration.async.core.UserInfo
import com.integration.core.applicationId
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
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

fun Pair<String, String>.isEmpty(): Boolean {
    return first.isBlank() || second.isBlank()
}

typealias AccountMap = Map<String,Any?>

fun Account?.isRestorable(savedAccount: Account?): Boolean {

        return when(this) {
                is BoldAccount -> liveRestorable(savedAccount as? BoldAccount)
                is AsyncAccount -> asyncRestorable(savedAccount as? AsyncAccount)
                is BotAccount -> botRestorable(savedAccount as? BotAccount)
                else -> false
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
        return AsyncAccount(this[AsyncSharedDataHandler.Access_key] as String, this[AsyncSharedDataHandler.App_id_Key] as String).apply {
                val userId = this@toAsyncAccount[AsyncSharedDataHandler.user_id_key] as String
                info.userInfo = (userId.takeIf { it.isNotEmpty() }?.let { UserInfo(it) } ?: UserInfo()).apply {
                        email = this@toAsyncAccount[AsyncSharedDataHandler.Email_key] as String
                        phoneNumber = this@toAsyncAccount[AsyncSharedDataHandler.Phone_Number_key] as String
                        firstName = this@toAsyncAccount[AsyncSharedDataHandler.First_Name_key] as String
                        lastName = this@toAsyncAccount[AsyncSharedDataHandler.Last_Name_key] as String
                        countryAbbrev = this@toAsyncAccount[AsyncSharedDataHandler.Country_Abbrev_key] as String
                }
        }
}

fun AsyncAccount.asyncRestorable(savedAccount: AsyncAccount?): Boolean {
        return apiKey == savedAccount?.apiKey &&
                getInfo().applicationId == savedAccount.getInfo().applicationId &&
                getInfo().userInfo.userId == savedAccount.getInfo().userInfo.userId
}

fun BotAccount.botRestorable(savedAccount: BotAccount?): Boolean  = savedAccount != null

fun BoldAccount.liveRestorable(savedAccount: BoldAccount?): Boolean {
        return (this.apiKey == savedAccount?.apiKey)
}