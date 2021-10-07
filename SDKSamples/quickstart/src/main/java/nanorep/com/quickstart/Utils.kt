package nanorep.com.quickstart

import android.content.Context
import com.nanorep.convesationui.structure.handlers.AccountInfoProvider
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.Completion

/**
 * Handles the shared preference interaction to save and retrieve last applied data to forms.
 *
 */
class PrevDataHandler {

    companion object {
        const val BotSharedName = "ChatDataPref.bot"

        const val ApiKey_key = "apiKey"
        const val Account_key = "accountKey"
        const val Kb_key = "kbKey"
        const val Server_key = "serverKey"
        const val Context_key = "contextKey"
    }

    private fun getBotData(context: Context): Map<String, Any?> {
        val shared = context.getSharedPreferences(BotSharedName, 0)
        return mapOf<String, Any?>(
            Account_key to shared.getString(Account_key, ""),
            Kb_key to shared.getString(Kb_key, ""),
            Server_key to shared.getString(Server_key, ""),
            ApiKey_key to shared.getString(ApiKey_key, ""),
            Context_key to shared.getStringSet(Context_key, mutableSetOf())
        )
    }

    private fun saveData(context: Context, sharedName: String, data: Map<String, Any?>) {
        val shared = context.getSharedPreferences(sharedName, 0)
        val editor = shared.edit()
        data.forEach { detail ->
            (detail.value as? String)?.run { editor.putString(detail.key, this) }
            (detail.value as? Set<String>)?.run { editor.putStringSet(detail.key, this) }
        }
        editor.apply() // commit()
    }

    fun saveChatData(context: Context, data: BotAccount) {
        saveData(context, BotSharedName, data.map())
    }

    fun getFormData(context: Context): Map<String, Any?> {
        return getBotData(context)
    }
}

///////////////////////////////////////////////////

class AccountHandler(var enableContinuity: Boolean = false) : AccountInfoProvider {

    private val accounts: MutableMap<String, AccountInfo> = mutableMapOf()

    override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {
        val account = if (enableContinuity) accounts[info.getApiKey()] else info
        // (account as? BoldAccount)?.skipPrechat()
        callback.onComplete(account ?: info)
    }

    override fun update(account: AccountInfo) {
        accounts[account.getApiKey()]?.run {
            update(account)
        } ?: kotlin.run {
            accounts[account.getApiKey()] = account
        }
    }
}

///////////////////////////////////////////////////

@Override
fun BotAccount.map(): Map<String, Any?> =
    mapOf(
        PrevDataHandler.Account_key to (account ?: ""),
        PrevDataHandler.Kb_key to (knowledgeBase ?: ""),
        PrevDataHandler.Server_key to (domain ?: ""),
        PrevDataHandler.ApiKey_key to apiKey,
        PrevDataHandler.Context_key to (
                contexts?.takeIf { it.isNotEmpty() }?.map { entry ->
                    "key= ${entry.key} value= ${entry.value}"
                }?.mapIndexed { index, str -> "$index:$str" }?.toHashSet() ?: setOf<String>()
                )
    )

