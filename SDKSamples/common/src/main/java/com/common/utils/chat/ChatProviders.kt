package com.common.utils.chat

import androidx.fragment.app.Fragment
import com.common.chatComponents.history.HistoryRepository
import com.common.utils.loginForms.RestoreState
import com.integration.core.securedInfo
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account

interface LoginData {

    /**
     * The Account (Bot/Bold/Async)
     */
    val account: Account?

    /**
     * Extra Account parameters to be submitted for the sample
     */
    val extraData: Map<String, Any?>?

    /**
     * The RestoreState of the account
     */
    val restoreState: RestoreState

    fun prepareAccount(securesInfo: String): Account? {
        return account?.apply {
            (this as? BoldAccount)?.let {
                it.info.securedInfo = securesInfo
            }
        }
    }
}

interface ChatProvider {

    /**
     * Holds the chat's login data
     */
    var loginData: LoginData

    /**
     * Returns encrypted info to be added to the Live account (if there is any)
     */
    fun getSecuredInfo(): String

    /**
     * Updates the History provider
     */
    fun updateHistoryRepo(historyRepository: HistoryRepository? = null, targetId: String? = null)

    /**
     * Being invoked when the chat fragment had been fetched and ready to be presented
     */
    var onChatLoaded: ((Fragment) -> Unit)?

    /**
     * Restores the chat (if hasChatController) for the current account
     */
    fun restore()

    /**
     * Creates the chat controller and starts the chat
     * @param chatBuilder optional injection of a custom ChatController.Builder
     * @param account the account to to build the chat for
     * When ready the chat fragment would be passed by 'onChatLoaded' invocation
     */
    fun create(chatBuilder: ChatController.Builder?): ChatController?

    /**
     * @return true if the chat controller exists and had not been destructed
     */
    fun hasChatController(): Boolean

    /**
     * To be called only after chat creation.
     * @return ChatController
     * @throws NullPointerException
     */
    @Throws(NullPointerException::class)
    fun getChatController() : ChatController

    /**
     * Clears the chat and release its resources
     */
    fun destruct()

    /**
     * Clears the history and frees its resources
     */
    fun clearHistory()

}