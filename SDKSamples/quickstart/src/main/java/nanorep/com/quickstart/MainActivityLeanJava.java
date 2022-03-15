package nanorep.com.quickstart;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.integration.core.StateEvent;
import com.nanorep.convesationui.structure.controller.ChatController;
import com.nanorep.convesationui.structure.controller.ChatEventListener;
import com.nanorep.convesationui.structure.controller.ChatLoadResponse;
import com.nanorep.convesationui.structure.controller.ChatLoadedListener;
import com.nanorep.nanoengine.AccountInfo;
import com.nanorep.nanoengine.bot.BotAccount;
import com.nanorep.nanoengine.model.configuration.ConversationSettings;
import com.nanorep.sdkcore.utils.NRError;

import org.jetbrains.annotations.NotNull;

/**
 * Please follow the related docs: https://logmein-bold-mobile.github.io/bold360-mobile-docs-android/docs/quick_start/
 * The samples app: https://github.com/bold360ai/bold360-mobile-samples-android.git
 */
public class MainActivityLeanJava extends AppCompatActivity implements ChatEventListener {

    BotAccount botAccount;

    ChatController chatController;

    private final static String TAG_LeanJavaActivity = "lean_java_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        // Fill your account credentials here:
        botAccount = new BotAccount("","nanorep","English","mobilestaging");

        chatController = createChat(botAccount, new ChatLoadedListener(){
            @Override
            public void onComplete(ChatLoadResponse response) {
                if (response.getError() != null) onError(response.getError());
                else {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    Fragment chatFragment = response.getFragment();
                    if (chatFragment == null)
                        onError(new NRError(NRError.EmptyError, "Chat UI failed to initialize"));
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
                .conversationSettings(new ConversationSettings())
                .chatEventListener(this)
                .build(botAccount, chatLoadedListener);
    }


    // Chat events handling by the app:
    @Override
    public void onAccountUpdate(@NonNull AccountInfo accountInfo) {}

    @Override
    public void onChatStateChanged(@NotNull StateEvent stateEvent) {
        Log.d(TAG_LeanJavaActivity, "onChatStateChanged: state " + stateEvent.getState());
    }

    @Override
    public void onPhoneNumberSelected(@NonNull String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

    @Override
    public void onUploadFileRequest() {
        Log.d(TAG_LeanJavaActivity,"ChatEventListener - onUploadFileRequest");

    }

    @Override
    public void onUrlLinkSelected(@NonNull String s) {
        Log.d(TAG_LeanJavaActivity,"ChatEventListener - onUrlLinkSelected");

    }

    @Override
    public void onError(@NonNull NRError nrError) {
        Log.d(TAG_LeanJavaActivity, "ChatEventListener - onError");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d(TAG_LeanJavaActivity, "ChatEventListener - onPointerCaptureChanged");
    }
}