package com.sdk.samples.topics



import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.structure.SingleLiveData
import com.nanorep.convesationui.structure.handlers.AccountInfoProvider
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.conversation.statement.StatementRequest
import com.nanorep.sdkcore.utils.Completion
import com.nanorep.sdkcore.utils.network.OnDataResponse
import com.sdk.samples.R
import kotlinx.android.synthetic.main.async_chat_config.*

open class BoldChatAsync : BasicChat() {



    protected val account: AsyncAccount by lazy {
        defaultAsyncAccount
    }
        @JvmName("account") get

    override fun getAccount(): Account {
        return account
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("async_shared", 0)

        ViewModelProvider(this).get(AsyncChatViewModel::class.java).apply {
            apiKey = prefs.getString("apiKey", "")?:""

        }
    }
*/


    companion object {
        val defaultAsyncAccount = AsyncAccount(
            "2307475884:2403340045369405:KCxHNTjbS7qDY3CVmg0Z5jqHIIceg85X:alphawd2",
            "mobile12345"
        ).apply {
            info.userInfo = UserInfo("1234567654321234569").apply {
                firstName = "First name"
                lastName = "Last name"
                email = "Email@Bold.com"
                phoneNumber = "123456"
            }
        }
    }
}

/*

class AsyncChatForm : Fragment() {


    val viewModel: AsyncChatViewModel by lazy {
        ViewModelProvider(activity!!).get(AsyncChatViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.async_chat_config,
            container,
            true
        )// super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiKey_edit.setText(viewModel.apiKey)
        restore_switch.isChecked = viewModel.restoreChat

        start_button.setOnClickListener {

            activity?.takeIf { this.isAdded }?.run {
                viewModel.apiKey = apiKey_edit.toString()
                viewModel.restoreChat = restore_switch.isChecked
            }
            fragmentManager?.popBackStack()
        }

    }


}

*/
/*
class AsyncChatViewModel : ViewModel() {
    var apiKey: String = ""
    var restoreChat: Boolean = false
    private val postRequest = SingleLiveData<Unit>()


    val onStart: SingleLiveData<Pair<StatementRequest, OnDataResponse<*>>>
        get() = postRequest

    fun onPostRequestCall(request: StatementRequest, callback: OnDataResponse<*>) {
        postRequest.value = Pair(request, callback)
    }

}

class AsyncAccountRecovery(context: Context) : AccountInfoProvider {

    val sharedPrefs = context.getSharedPreferences("async_shared", 0)

    fun saveAccount(account: AsyncAccount){

    }

    fun restoreAccount() : AsyncAccount {

    }

    override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {
        return restoreAccount()
    }
    }
*//*



*/
