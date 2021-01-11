package com.common.chatComponents

import android.util.Log
import com.integration.core.FormResults
import com.nanorep.convesationui.structure.UploadNotification
import com.nanorep.convesationui.structure.controller.ChatNotifications
import com.nanorep.sdkcore.utils.DispatchContinuation
import com.nanorep.sdkcore.utils.Notifiable
import com.nanorep.sdkcore.utils.Notification
import com.nanorep.sdkcore.utils.Notifications

class NotificationsReceiver : Notifiable {

    override fun onNotify(notification: Notification, dispatcher: DispatchContinuation) {
        when (notification.notification) {
            ChatNotifications.PostChatFormSubmissionResults, ChatNotifications.UnavailabilityFormSubmissionResults -> {
                val results = notification.data as FormResults?
                if (results != null) {
                    Log.i(
                        NotificationsReceiver_TAG, "Got notified for form results for form: " +
                                results.data +
                                if (results.error != null) ", with error: " + results.error!! else ""
                    )


                } else {
                    Log.w(NotificationsReceiver_TAG, "Got notified for form results but results are null")
                }
            }

            Notifications.UploadEnd,
            Notifications.UploadStart,
            Notifications.UploadProgress,
            Notifications.UploadFailed -> {
                val uploadNotification = notification as UploadNotification
                Log.d(
                    NotificationsReceiver_TAG, "Got upload event ${uploadNotification.notification} on " +
                            "file: ${uploadNotification.uploadInfo.name}"
                )
            }
        }
    }

    companion object {
        const val NotificationsReceiver_TAG = "NotificationsReceiver"
    }
}