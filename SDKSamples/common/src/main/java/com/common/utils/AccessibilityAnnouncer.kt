package com.common.utils

import android.content.Context
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.FeedbackElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.IncomingElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.OutgoingElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.QuickOptionsElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.SystemMessageElement
import com.nanorep.convesationui.structure.elements.ChatElement.Companion.UploadElement
import com.nanorep.convesationui.structure.elements.ElementModel
import com.nanorep.sdkcore.utils.obtainAccessibilityEvent
import com.nanorep.sdkcore.utils.sendAccessibilityEvent
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.common.R


class AccessibilityAnnouncer(context: Context) {

    private var wContext = context.weakRef()
    private val sync = Unit

    fun announce(msg: String?) {
       // Log.d("listen", "accessibility msg: $msg");
        wContext.get()?.takeUnless { msg.isNullOrBlank() }?.let {
            synchronized(sync) {
                Log.i("Accessibility", ">>> Accessibility announce: $msg")
                it.sendAccessibilityEvent(it.obtainAccessibilityEvent(
                        AccessibilityEvent.TYPE_ANNOUNCEMENT, msg))
            }
        }
    }

    fun announce(resourceId: Int) {
        wContext.get()?.takeUnless { resourceId == -1 }?.getString(resourceId)?.let {
            announce(it)
        }
    }

    fun announce(elementModel: ElementModel) {


        val res = when (elementModel.elemType) {
            OutgoingElement -> R.string.outgoing_element
            IncomingElement -> R.string.incoming_element
            SystemMessageElement -> R.string.system_element
            QuickOptionsElement -> R.string.options_element
            UploadElement -> R.string.upload_element
            FeedbackElement -> R.string.feedback_element
            else -> -1
        }

        if (res != -1) {
            announce(wContext.get()?.getString(res))
        }
    }
}
