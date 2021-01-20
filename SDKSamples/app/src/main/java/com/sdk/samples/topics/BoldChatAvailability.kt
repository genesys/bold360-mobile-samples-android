package com.sdk.samples.topics

//
//open class BoldChatAvailability : BoldChat() {
//
//    private val availabilityViewModel: CheckAvailabilityViewModel by lazy {
//        ViewModelProvider(this).get(CheckAvailabilityViewModel::class.java)
//    }
//
//    private fun loadAvailabilityCheck() {
//
//        availabilityViewModel.apply {
//            account = getAccount_old() as BoldAccount
//
//            observeResults(this@BoldChatAvailability,
//                Observer { results ->
//                    results?.run {
//                        if (isAvailable) {
//                            departmentId.takeIf { it > 0 }?.let {
//                                account.addExtraData(SessionInfoKeys.Department to results.departmentId)
//                            }
//
//                            prepareAccount(account)
//
//                            createChat()
//                        }
//                    }
//                })
//        }
//
//        supportFragmentManager.beginTransaction()
//            .add(R.id.basic_chat_view, BoldAvailability(), AvailabilityTag)
//            .addToBackStack(AvailabilityTag)
//            .commit()
//    }
//
//    protected open fun prepareAccount(account: BoldAccount) {
//        //account.skipPrechat() //Uncomment to start chat immediately without displaying prechat form to the user.
//
//        /*//>>> uncomment to enable passing preconfigured encrypted info, that enables chat creation,
//                if your account demands it.
//                Replace current text with your Secured string.
//
//           account.info.securedInfo = "this is an encrypted content. Don't read"
//        */
//    }
//
//    override fun startChat(savedInstanceState: Bundle?) {
//        loadAvailabilityCheck()
//    }
//
//    override fun onChatStateChanged(stateEvent: StateEvent) {
//        super.onChatStateChanged(stateEvent)
//
//        when (stateEvent.state) {
//            StateEvent.Idle, StateEvent.Unavailable -> {
//                removeChatFragment()
//
//                //-> trigger the observer that was assigned to this viewModel to trogger
//                //  refresh of chat availability status.
//                availabilityViewModel.refresh(Event(Empty))
//            }
//        }
//    }
//
//    override fun getChatBuilder(): ChatController.Builder? {
//        return super.getChatBuilder()?.apply {
//            this.chatUIProvider(ChatUIProvider(this@BoldChatAvailability).apply {
//                chatInputUIProvider.uiConfig.showUpload = false
//            })
//        }
//    }
//
//    companion object {
//        const val AvailabilityTag = "AvailabilityTag"
//    }
//}
//
