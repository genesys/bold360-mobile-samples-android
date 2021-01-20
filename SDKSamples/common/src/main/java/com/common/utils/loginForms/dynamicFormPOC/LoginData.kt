package com.common.utils.loginForms.dynamicFormPOC

import com.google.gson.JsonObject

data class LoginData (

    /**
     * The Account (Bot/Bold/Async)
     */
    val account: JsonObject?,

    /**
     * The RestoreState of the account
     */
    val restoreRequest: Boolean

)