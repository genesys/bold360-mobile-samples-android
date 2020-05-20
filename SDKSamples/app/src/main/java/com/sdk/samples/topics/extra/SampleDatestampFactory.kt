package com.sdk.samples.topics.extra

import android.content.Context
import com.nanorep.nanoengine.model.configuration.DatestampFormatFactory
import com.sdk.samples.R
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class SampleDatestampFactory : DatestampFormatFactory {

    private val simpleDateFormat = SimpleDateFormat("EEEEE MMM HH:mm:ss", DateFormatSymbols(Locale.getDefault()).apply {
        setWeekdays(arrayOf(
            "Unused",
            "Sad Sunday",
            "Manic Monday",
            "Thriving Tuesday",
            "Wet Wednesday",
            "Total Thursday",
            "Fat Friday",
            "Super Saturday"))
    })

    override fun formatDate(datestamp: Long): String {
        return simpleDateFormat.format(datestamp)
    }
}