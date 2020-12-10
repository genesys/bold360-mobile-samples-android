package com.sdk.samples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.sdk.samples.common.ChatType
import com.sdk.samples.topics.Accounts
import java.lang.reflect.InvocationTargetException

class SamplesViewModel : ViewModel() {

    var account: Account? = null
    get() = when(chatType) {
        ChatType.AsyncChat -> field ?: Accounts.defaultAsyncAccount
        ChatType.LiveChat -> field ?: Accounts.defaultBoldAccount
        else -> field ?: Accounts.defaultBotAccount
    }

    var chatController: ChatController? = null

    var restoreRequest = false
    var restoreable: Boolean = false

    @ChatType
    private var chatType: String = ChatType.BotChat

    companion object {

        private var myViewModel: SamplesViewModel? = null

        fun getInstance() : SamplesViewModel {
            if (myViewModel == null) {
                myViewModel = SamplesViewModel()
            }
            return myViewModel!!
        }
    }

    override fun onCleared() {
        chatController = null
        super.onCleared()
    }

}

class SingletonSamplesViewModelFactory(vmInstance: SamplesViewModel) : NewInstanceFactory() {

    private val samplesViewModel: SamplesViewModel = vmInstance
    private val viewModelFactory: MutableMap<Class<out ViewModel>, ViewModel> = mutableMapOf()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        viewModelFactory[modelClass] = samplesViewModel

        if (SamplesViewModel::class.java.isAssignableFrom(modelClass)) {

            val shareVM: SamplesViewModel

            if (viewModelFactory.containsKey(modelClass)) {
                shareVM = viewModelFactory[modelClass] as SamplesViewModel
            } else {
                shareVM = try {
                    modelClass.getConstructor(Runnable::class.java).newInstance(
                        Runnable { viewModelFactory.remove(modelClass) }) as SamplesViewModel

                } catch (e: Exception) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InstantiationException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                }
                viewModelFactory[modelClass] = shareVM
            }
            return shareVM as T
        }

        return super.create(modelClass)
    }

}