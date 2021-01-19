package com.common.utils.loginForms

import com.google.gson.JsonObject

interface LoginData {

    /**
     * The Account (Bot/Bold/Async)
     */
    val account: JsonObject?

    /**
     * Extra Account parameters to be submitted for the sample
     */
    val extraData: Map<String, Any?>?

    /**
     * The RestoreState of the account
     */
    val restoreState: RestoreState

}