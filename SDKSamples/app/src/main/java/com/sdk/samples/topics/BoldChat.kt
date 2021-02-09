package com.sdk.samples.topics

import com.common.topicsbase.BasicChat
import com.common.utils.ChatForm.defs.ChatType
import com.integration.core.securedInfo
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account

abstract class BoldChat : BasicChat() {

    override var chatType: String = ChatType.Live

    override fun prepareAccount(): Account = (account as BoldAccount).apply { info.securedInfo = getSecuredInfo() }

}