package com.common.utils.loginForms.dynamicFormPOC

import com.google.gson.JsonObject

interface LoginData {

    /**
     * The Account (Bot/Bold/Async)
     */
    val account: JsonObject?

    /**
     * Extra Account parameters to be submitted for the sample
     */
    val extraData: JsonObject?

    /**
     * The RestoreState of the account
     */
    val restoreRequest: Boolean

}