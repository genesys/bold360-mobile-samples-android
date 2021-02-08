package com.sdk.samples.topics

import com.common.topicsbase.BasicChat
import com.common.utils.forms.defs.ChatType
import com.integration.core.securedInfo
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.SystemUtil

abstract class BoldChat : BasicChat() {

    override fun prepareAccount(): Account = (account as BoldAccount).apply { info.securedInfo = getSecuredInfo() }

    override var chatType: String = ChatType.Live

}