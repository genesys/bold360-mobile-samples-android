//package com.sdk.samples.topics
//
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.drawable.Drawable
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.common.utils.forms.Accounts.defaultBoldAccount
//import com.nanorep.convesationui.structure.controller.ChatAvailability
//import com.nanorep.sdkcore.utils.snack
//import com.sdk.samples.R
//import kotlinx.android.synthetic.main.availability_activity.*
//
//class CheckAvailability : AppCompatActivity() {
//
//    private var chipUncheckedIcon: Drawable? = null
//
//    val account = defaultBoldAccount
//
//    /*val model:AvailabilityViewModel? by lazy {
//        activity?.let{ ViewModelProviders.of(it).get(AvailabilityViewModel::class.java)}
//    }*/
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContentView(R.layout.availability_activity)
//
//        chipUncheckedIcon = action_chip.closeIcon
//        action_chip.closeIcon = resources.getDrawable(R.drawable.chat_channel)
//        action_chip.setOnCloseIconClickListener { _ ->
//            ChatAvailability.checkAvailability(account, callback = object : ChatAvailability.Callback{
//                override fun onComplete(result: ChatAvailability.AvailabilityResult) {
//                    if(this@CheckAvailability.isFinishing || this@CheckAvailability.isChangingConfigurations) return
//
//                    action_chip.isSelected = result.isAvailable
//                    action_chip.chipIcon = if (action_chip.isSelected) action_chip.checkedIcon else chipUncheckedIcon
//
//                    result.reason?.run {
//                        action_chip.snack("chat is not available due to $this", backgroundColor = Color.DKGRAY,
//                            disableSwipes = false)
//                    }
//                }
//            })
//        }
//
//        action_chip.setOnClickListener { chip ->
//            if (chip.isSelected) {
//                startActivity(Intent("com.sdk.sample.action.BOLD_CHAT").putExtra("title", intent.getStringExtra("title")))
//            }
//        }
//
//
//    }
//
//
//    override fun onResume() {
//        super.onResume()
//
//        action_chip.performCloseIconClick()
//
//    }
//}
//
///*
//class ChatAvailability {
//    companion object {
//
//        var lastRes: Boolean = true
//
//        fun checkAvailability(boldAccount: Account, callback: (DataStructure<Boolean>) -> Unit) {
//            lastRes = !lastRes
//            callback(DataStructure(lastRes))
//        }
//    }
//}*/
