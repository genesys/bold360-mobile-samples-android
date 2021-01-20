package com.common.topicsbase

//
//abstract class RestorationContinuity : History() {
//
//    override val account: Account
//        get() = accountData.toBotAccount()
//
//    override var formsParams = AsyncExtraData
//
//    override var chatType = ChatType.None
//
//    override val onChatTypeChanged: ((chatType: String) -> Unit)?
//        get() = { chatType ->
//
//            when (chatType) {
//                ChatType.None -> getAccount_old()
//            }
//
//        }
//
//    /**
//     * Reloads the login forms according to the ChatType
//     */
//    private fun reloadForms() {
//        supportFragmentManager.fragments.clear()
//        Log.i("RestoreSample", "ChatController hadn't been destructed")
//        val accountFormController = AccountFormController(R.id.basic_chat_view, supportFragmentManager.weakRef())
//
//        if (hasChatController()) {
//            addFormsParam(EnableRestore)
//        }
//
//        accountFormController.login()
//    }
//
//    override fun startChat(savedInstanceState: Bundle?) {
//
//        getAccount_old()?.getGroupId()?.let {
//            updateHistoryRepo(targetId = it)
//        }
//
//        super.startChat(savedInstanceState)
//
//    }
//
//    override fun onChatUIDetached() {
//
//        // if there are no fragments at the backStack we represent the forms at the Sample context
//        if (supportFragmentManager.backStackEntryCount == 0) {
//            reloadForms()
//        }
//    }
//
//    fun onRestoreFailed(reason: String) {
//        toast( baseContext, reason, Toast.LENGTH_SHORT )
//        onBackPressed()
//    }
//
//    override fun onBackPressed() {
//
//        when {
//
//            supportFragmentManager.fragments.isEmpty() || supportFragmentManager.getCurrent()?.tag == topicTitle -> {
//                removeChatFragment()
//                supportFragmentManager.executePendingTransactions()
//            }
//
//            else -> {
//                supportFragmentManager.popBackStackImmediate()
//                if (!isFinishing) finishIfLast()
//            }
//
//        }
//    }
//
//}