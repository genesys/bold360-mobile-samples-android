package nanorep.com.quickstartjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.integration.core.StateEvent;
import com.nanorep.convesationui.structure.controller.ChatController;
import com.nanorep.convesationui.structure.controller.ChatEventListener;
import com.nanorep.convesationui.structure.controller.ChatLoadResponse;
import com.nanorep.convesationui.structure.controller.ChatLoadedListener;
import com.nanorep.nanoengine.AccountInfo;
import com.nanorep.nanoengine.bot.BotAccount;
import com.nanorep.nanoengine.model.configuration.ConversationSettings;
import com.nanorep.sdkcore.utils.NRError;


/**
 * Please follow the related docs: https://logmein-bold-mobile.github.io/bold360-mobile-docs-android/docs/quick_start/
 * The samples app: https://github.com/bold360ai/bold360-mobile-samples-android.git
 */
public class MainActivity extends AppCompatActivity implements ChatEventListener {

    BotAccount botAccount;

    ChatController chatController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fill your account credentials here:
        botAccount = new BotAccount("","","","");

        chatController = createChat(botAccount, new ChatLoadedListener(){
            @Override
            public void onComplete(ChatLoadResponse response) {
                if (response.getError() != null) onError(response.getError());
                else {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    Fragment chatFragment = response.getFragment();
                    if (chatFragment == null)
                        onError(new NRError(NRError.EmptyError, "Chat UI failed to init"));
                    else getSupportFragmentManager().beginTransaction().replace(R.id.content_main, chatFragment).commit();
                }
            }
        });
    }

    /**
     *
     * @param botAccount - The wanted account
     * @param chatLoadedListener - a callback of the chat creation
     * @return ChatController
     */
    ChatController createChat(BotAccount botAccount, ChatLoadedListener chatLoadedListener) {

        return new ChatController.Builder(this)
                .conversationSettings(new ConversationSettings()).chatEventListener(this)
                .chatEventListener(this)
                .build(botAccount, chatLoadedListener);
    }


    // Chat events handling by the app:
    @Override
    public void onAccountUpdate(@NonNull AccountInfo accountInfo) {}

    @Override
    public void onChatStateChanged(@NonNull StateEvent stateEvent) {
        Log.d("ChatEventListener","onChatStateChanged");
    }

    @Override
    public void onPhoneNumberSelected(@NonNull String s) {
        Log.d("ChatEventListener","onPhoneNumberSelected");

    }

    @Override
    public void onUploadFileRequest() {
        Log.d("ChatEventListener","onUploadFileRequest");

    }

    @Override
    public void onUrlLinkSelected(@NonNull String s) {
        Log.d("ChatEventListener","onUrlLinkSelected");

    }

    @Override
    public void onError(@NonNull NRError nrError) {
        Log.d("ChatEventListener","onError");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d("ChatEventListener","onPointerCaptureChanged");
    }
}