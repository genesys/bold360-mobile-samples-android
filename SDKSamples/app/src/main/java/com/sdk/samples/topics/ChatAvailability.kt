package com.sdk.samples.topics

/*
class CheckAvailability : SampleActivity() {

    private var chipUncheckedIcon: Drawable? = null

    val account: BoldAccount
    get() = getAccount() as BoldAccount

    override val containerId: Int
        get() =

    override val chatType: String
        get() = ChatType.Live

    override fun startChat(savedInstanceState: Bundle?) {

        chipUncheckedIcon = action_chip.closeIcon

        action_chip.closeIcon = ContextCompat.getDrawable(baseContext, R.drawable.chat_channel)
        action_chip.setOnCloseIconClickListener { _ ->
            ChatAvailability.checkAvailability(account, callback = object : ChatAvailability.Callback{
                override fun onComplete(result: ChatAvailability.AvailabilityResult) {
                   // if(this@CheckAvailability.isFinishing || this@CheckAvailability.isChangingConfigurations) return

                    action_chip.isSelected = result.isAvailable
                    action_chip.chipIcon = if (action_chip.isSelected) action_chip.checkedIcon else chipUncheckedIcon

                    result.reason?.run {
                        action_chip.snack("chat is not available due to $this", backgroundColor = Color.DKGRAY,
                            disableSwipes = false)
                    }
                }
            })
        }

        action_chip.setOnClickListener { chip ->
            if (chip.isSelected) {
                startActivity(Intent("com.sdk.sample.action.BOLD_CHAT").putExtra("title", topicTitle))
            }
        }
    }

    override val extraFormsParams = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.availability_activity)

    }


    override fun onResume() {
        super.onResume()

        action_chip.performCloseIconClick()

    }
}*/
