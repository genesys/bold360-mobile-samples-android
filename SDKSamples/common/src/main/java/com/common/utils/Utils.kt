package com.common.utils

import android.annotation.SuppressLint
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