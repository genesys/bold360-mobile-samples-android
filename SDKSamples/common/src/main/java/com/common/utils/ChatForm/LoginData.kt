package com.common.utils.ChatForm

import com.google.gson.JsonObject

data class LoginData (

    /**
     * The Account (Bot/Bold/Async)
     */
    val account: JsonObject?,

    /**
     * true if there was a restoration request
     */
    val restoreRequest: Boolean

)