package com.common.utils

import android.content.Context
import android.util.Log
import com.common.chatComponents.history.RoomHistoryProvider
import com.nanorep.convesationui.structure.elements.ElementModel

open class InterceptData(val type: Int, var liveScope: Boolean = false)


class ElementsInterceptor(context: Context, private val announcer: AccessibilityAnnouncer)
    : RoomHistoryProvider(context) {

    var interceptionRules: List<InterceptData> = listOf()
    var announceRules: List<InterceptData> = listOf()

    override fun intercept(element: ElementModel): Boolean {
        Log.d("Interception", "Got interception call with ${element::class.simpleName}")

        fun List<InterceptData>.findRule(type: Int, liveScope:Boolean): InterceptData? {
            return this.filter { it.type == type }.find { rule ->
                // if rule set to liveScope, only on live chat the announcer should be activated for this element
                !rule.liveScope || rule.liveScope == liveScope
            }
        }

        announceRules.findRule(element.elemType, element.elemScope.isLive)?.let {
            announcer.announce(element)
        }

        return (interceptionRules.findRule(element.elemType, element.elemScope.isLive) != null).also {
            if (it) {
                Log.w("Interception", "Intercepting ${element::class.simpleName}")
            }
        }
    }
}