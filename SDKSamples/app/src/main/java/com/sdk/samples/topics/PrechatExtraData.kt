package com.sdk.samples.topics

import androidx.annotation.NonNull
import com.integration.core.annotations.VisitorDataKeys
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.handlers.AccountInfoProvider
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.sdkcore.utils.Completion
import com.sdk.samples.topics.extra.SimpleAccountProvider
import java.util.*

class PrechatExtraData : BotChat() {

    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder().accountProvider( Companion )
    }


    companion object : SimpleAccountProvider() {

        const val BOLD_DEPARTMENT = "2278985919139590636"
        const val DemoFirstName = "Bold"
        const val DemoLastName = "360"

        override fun addAccount(account: AccountInfo) {
            (account as? BoldAccount)?.apply {
                addExtraData (
                    VisitorDataKeys.Department to BOLD_DEPARTMENT,
                    VisitorDataKeys.FirstName to DemoFirstName,
                    VisitorDataKeys.LastName to DemoLastName)
            }
            super.addAccount(account)
        }
    }
}
