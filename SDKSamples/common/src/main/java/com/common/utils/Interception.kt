package com.common.utils

import android.content.Context
import com.common.chatComponents.history.RoomHistoryProvider
import com.nanorep.convesationui.structure.elements.ElementModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

//class InterceptRule(val type: Int, var scope: StatementScope? = StatementScope.UnknownScope)
open class InterceptData(val type: Int, var liveScope: Boolean = false)


@ExperimentalCoroutinesApi
class ElementsInterceptor(context: Context, val announcer: AccessibilityAnnouncer)
    : RoomHistoryProvider(context) {


    var interceptionRules: List<InterceptData> = listOf()
    var announceRules: List<InterceptData> = listOf()


    override fun intercept(element: ElementModel): Boolean {

        announceRules.filter { it.type == element.elemType }.find { rule ->
            rule.liveScope == element.elemScope.isLive
        }?.let {
            announcer.announce(element)
        }

        return interceptionRules.filter { it.type == element.elemType }.find { rule ->
            rule.liveScope == element.elemScope.isLive
        } != null
    }

}