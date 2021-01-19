package com.common.utils.loginForms.accountUtils

/*

@Override
internal fun BotAccount.map(): Map<String, Any?> =
        mapOf(
                BotSharedDataHandler.Account_key to (account.orEmpty()),
                BotSharedDataHandler.Kb_key to (knowledgeBase.orEmpty()),
                BotSharedDataHandler.Server_key to (domain.orEmpty()),
                Access_key to apiKey,
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

internal fun Pair<String, String>.isEmpty(): Boolean {
    return first.isBlank() || second.isBlank()
}

internal fun AccountMap.equalsTo(other: AccountMap): Boolean {

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

internal fun Account?.orDefault(@ChatType chatType: String): Account? {
        return this ?: when (chatType) {
                ChatType.Live -> Accounts.defaultBoldAccount
                ChatType.Async -> Accounts.defaultAsyncAccount
                ChatType.Bot -> Accounts.defaultBotAccount
                else -> null
        }
}

private typealias AccountMap = Map<String,Any?>

internal fun Account?.isRestorable(savedAccount: Account?): Boolean {

        return when(this) {
                is BoldAccount -> liveRestorable(savedAccount as? BoldAccount)
                is AsyncAccount -> asyncRestorable(savedAccount as? AsyncAccount)
                is BotAccount -> botRestorable(savedAccount as? BotAccount)
                else -> false
        }
}

internal fun AccountMap.toBotAccount(): BotAccount? {
        return (this[BotSharedDataHandler.ApiKey_key] as? String)?.let { apiKey ->
                BotAccount(apiKey,
                        this[BotSharedDataHandler.Account_key] as String,
                        this[BotSharedDataHandler.Kb_key] as String,
                        this[BotSharedDataHandler.Server_key] as String,
                        this[BotSharedDataHandler.Context_key] as? Map<String, String>?).apply {
                                (get(BotSharedDataHandler.Welcome_key) as? String)
                                        ?.takeUnless { it.isEmpty() }?.let { welcomeMessage = it }
                        }
        }
}

internal fun AccountMap.toLiveAccount(): BoldAccount? {
        return (this[LiveSharedDataHandler.Access_key] as? String)?.let { BoldAccount(it) }
}

internal fun AccountMap.toAsyncAccount(): AsyncAccount? {
        val accessKey = this[AsyncSharedDataHandler.Access_key] as? String
        return accessKey?.let {
                AsyncAccount(it, this[AsyncSharedDataHandler.App_id_Key] as? String.orEmpty()).apply {
                        val userId = this@toAsyncAccount[AsyncSharedDataHandler.user_id_key] as? String.orEmpty()
                        info.userInfo =
                                (userId.takeIf { userId.isNotEmpty() }?.let { UserInfo(userId) } ?: UserInfo()).apply {
                                        email = this@toAsyncAccount[AsyncSharedDataHandler.Email_key] as? String.orEmpty()
                                        phoneNumber = this@toAsyncAccount[AsyncSharedDataHandler.Phone_Number_key] as? String.orEmpty()
                                        firstName = this@toAsyncAccount[AsyncSharedDataHandler.First_Name_key] as? String.orEmpty()
                                        lastName = this@toAsyncAccount[AsyncSharedDataHandler.Last_Name_key] as? String.orEmpty()
                                        countryAbbrev = this@toAsyncAccount[AsyncSharedDataHandler.Country_Abbrev_key] as? String.orEmpty()
                                }
                }
        }
}

internal fun AsyncAccount.asyncRestorable(savedAccount: AsyncAccount?): Boolean {
        return apiKey == savedAccount?.apiKey &&
                getInfo().applicationId == savedAccount.getInfo().applicationId &&
                getInfo().userInfo.userId == savedAccount.getInfo().userInfo.userId
}

internal fun BotAccount.botRestorable(savedAccount: BotAccount?): Boolean = savedAccount?.info?.userInfo?.userId == info.userInfo.userId

internal fun BoldAccount.liveRestorable(savedAccount: BoldAccount?): Boolean {
        return (this.apiKey == savedAccount?.apiKey)
}*/
