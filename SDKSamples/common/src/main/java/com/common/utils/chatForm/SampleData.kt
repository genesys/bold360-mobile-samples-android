package com.common.utils.chatForm

import com.google.gson.JsonObject

data class SampleData (

    /**
     * The Account (Bot/Bold/Async)
     */
    val account: JsonObject?,

    /**
     * true if there was a restoration request
     */
    val restoreRequest: Boolean

)