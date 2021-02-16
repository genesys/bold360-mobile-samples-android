package com.common.utils

import android.annotation.SuppressLint
import android.util.Log
import com.common.utils.chatForm.ChatForm
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.nanorep.nanoengine.model.configuration.DatestampFormatFactory
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class SampleDatestampFactory : DatestampFormatFactory {

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("EEEEE MMM HH:mm:ss", DateFormatSymbols(Locale.getDefault()).apply {
        weekdays = arrayOf(
            "Unused",
            "Sad Sunday",
            "Manic Monday",
            "Thriving Tuesday",
            "Wet Wednesday",
            "Total Thursday",
            "Fat Friday",
            "Super Saturday")
    })

    override fun formatDate(datestamp: Long): String {
        return simpleDateFormat.format(datestamp)
    }
}

fun JsonElement.toObject(catchEmpty: Boolean = false): JsonObject? {
    return try {
        this.asJsonObject
    } catch ( exception : IllegalStateException) {
        // being thrown by the "asJsonObject" casting
        Log.w(ChatForm.TAG, exception.message ?: "Unable to parse field")
        if (catchEmpty) JsonObject() else null
    }
}