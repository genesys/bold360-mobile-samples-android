package com.conversation.demo;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.conversation.demo.forms.AccountForm;
import com.conversation.demo.forms.AccountListener;
import com.conversation.demo.forms.DemoForm;
import com.integration.bold.boldchat.core.FormData;
import com.integration.bold.boldchat.core.PostChatData;
import com.integration.core.FormResults;
import com.integration.core.StateEvent;
import com.nanorep.convesationui.bold.ui.FormListener;
import com.nanorep.convesationui.structure.FriendlyDatestampFormatFactory;
import com.nanorep.convesationui.structure.UiConfigurations;
import com.nanorep.convesationui.structure.controller.*;
import com.nanorep.convesationui.structure.feedback.FeedbackUIAdapter;
import com.nanorep.convesationui.structure.feedback.FeedbackViewDummy;
import com.nanorep.convesationui.structure.handlers.AccountInfoProvider;
import com.nanorep.convesationui.structure.history.FetchDirection;
import com.nanorep.convesationui.structure.history.HistoryListener;
import com.nanorep.convesationui.structure.history.HistoryProvider;
import com.nanorep.convesationui.structure.providers.ChatUIProvider;
import com.nanorep.convesationui.structure.providers.FeedbackUIProvider;
import com.nanorep.convesationui.structure.providers.IncomingElementUIProvider;
import com.nanorep.convesationui.structure.providers.OutgoingElementUIProvider;
import com.nanorep.convesationui.views.carousel.CarouselItemsUIAdapter;
import com.nanorep.convesationui.views.chatelement.BubbleContentUIAdapter;
import com.nanorep.nanoengine.Account;
import com.nanorep.nanoengine.AccountInfo;
import com.nanorep.nanoengine.chatelement.*;
import com.nanorep.nanoengine.model.MultiAnswerItem;
import com.nanorep.nanoengine.model.configuration.ConversationSettings;
import com.nanorep.nanoengine.model.configuration.StyleConfig;
import com.nanorep.nanoengine.model.configuration.TimestampStyle;
import com.nanorep.sdkcore.model.StatementScope;
import com.nanorep.sdkcore.model.StatementStatus;
import com.nanorep.sdkcore.utils.*;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import org.jetbrains.anko.ToastsKt;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.integration.core.StateEvent.*;
import static com.nanorep.sdkcore.model.StatementModels.StatusPending;
import static com.nanorep.sdkcore.model.StatementModels.isLive;
import static org.jetbrains.anko.ToastsKt.toast;


public class MainActivity extends AppCompatActivity implements ChatEventListener, AccountListener {

    public static final String My_TAG = "MainActivity";
    public static final String CONVERSATION_FRAGMENT_TAG = "conversation_fragment";
    public static final String DEMO_FORM_TAG = "demo_form_fragment";


    public static final int HistoryPageSize = 8;

    private ProgressBar progressBar;
    private Menu menu;

    private Notifiable notificationsReceiver = new NotificationsReceiver();

    private Map<String, AccountInfo> accounts = new HashMap<>();

    private ChatController chatController;
    private MyHistoryProvider historyProvider;

    /*!! ChatController's providers kept as members to make sure their object will be kept alive
       (chatController handles those listeners and providers as weak references, which means they
       may be released otherwise) */
    private MyAccountInfoProvider accountInfoProvider;
    private MyFormProvider formProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);

        accountInfoProvider = new MyAccountInfoProvider();
        formProvider = new MyFormProvider(this);
        historyProvider = new MyHistoryProvider();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return false;
            case R.id.bold_end:
                chatController.endChat(false);
                return true;
            default:
                break;
        }

        return false;
    }

    private void clearsAllResources() {
        try {
            chatController.unsubscribeNotifications(notificationsReceiver);
            chatController.terminateChat();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        if (isFinishing()) {
            clearsAllResources();
        }

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        showWaitIndication(View.GONE);
        super.onBackPressed();
    }

    @Override
    public void onReady(@NonNull Account account) {
        showWaitIndication(View.VISIBLE);

        if (!accounts.containsKey(account.apiKey())) {
            accounts.put(account.apiKey(), account);
        }

        historyProvider.accountId = account.getApiKey();

        this.chatController = createChat(account);

        chatController.subscribeNotifications(notificationsReceiver, ChatNotifications.PostChatFormSubmissionResults,
                ChatNotifications.UnavailabilityFormSubmissionResults);
    }

    public void onChatClick(View view){
        View focused = getCurrentFocus();

        if (focused != null) {
            focused.clearFocus();
        }

        String chatType = view.getId() == R.id.bold_chat ? ChatType.LiveChat : ChatType.BotChat;

        Fragment accountForm = AccountForm.create(chatType);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, accountForm);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @NonNull
    private ChatController createChat(Account account) {

        ConversationSettings settings = new ConversationSettings()
                .speechEnable(true)
                .enableMultiRequestsOnLiveAgent(true)
                .timestampConfig(true, new TimestampStyle("dd.MM hh:mm:ss",
                        10, Color.parseColor("#33aa33"), null))
                .datestamp(true, new FriendlyDatestampFormatFactory(this));

        return new ChatController.Builder(this)
                .conversationSettings(settings)
                .chatEventListener(this)
                .historyProvider(historyProvider)
                .accountProvider(accountInfoProvider)
                .chatUIProvider(getCustomisedChatUI())
                .formProvider(formProvider)
                .build(account, new ChatLoadedListener(){

                    @Override
                    public void onComplete(ChatLoadResponse result) {

                        NRError error = result.getError();

                        if(error != null) {
                            onError(error);
                            if (! (error.isConversationError() || error.isServerConnectionNotAvailable())) {
                                openConversationFragment(result.getFragment());
                            }

                        } else {
                            openConversationFragment(result.getFragment());
                        }
                        showWaitIndication(View.GONE);
                    }
                });
    }

    @Override
    public void onAccountUpdate(@NonNull AccountInfo accountInfo) {
        AccountInfo savedAccount = getAccountInfo(accountInfo.getApiKey());
        if(savedAccount != null) {
            savedAccount.updateInfo(accountInfo.getInfo());
        } else {
            Log.w(CONVERSATION_FRAGMENT_TAG, "Got account update for account that is currently not " +
                    "in accounts list\nadding account to saved accounts list");
            accounts.put(accountInfo.getApiKey(), accountInfo);
        }
    }

    private void openConversationFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(fragmentManager == null || fragmentManager.isStateSaved() ||
                fragmentManager.findFragmentByTag(CONVERSATION_FRAGMENT_TAG) != null) return;

        fragmentManager.beginTransaction()
                .replace(R.id.content_main, fragment, CONVERSATION_FRAGMENT_TAG)
                .addToBackStack(CONVERSATION_FRAGMENT_TAG)
                .commit();
    }

    private void showWaitIndication(int state) {
        progressBar.setVisibility(state);
    }

    private AccountInfo getAccountInfo(String apiKey) {
        return accounts.get(apiKey);
    }


    @SuppressLint("ResourceType")
    @Override
    public void onError(@NonNull NRError error) {

        String reason = error.getReason();

        switch (error.getErrorCode()){

            case NRError.ConversationCreationError:

                notifyConversationError(error);

                if(reason!=null && reason.equals(NRError.ConnectionException)) {
                    notifyConnectionError();
                }

                break;

            case NRError.StatementError:

                if(error.isConversationError()){
                    notifyConversationError(error);

                } else {
                    notifyStatementError(error);
                }

                break;

            default:
                /*all other errors will be handled here. Demo implementation, displays a toast and
                  writes to the log.
                 if needed the error.getErrorCode() and sometimes the error.getReason() can provide
                 the details regarding the error
                 */
                Log.e("App-ERROR", error.toString());

                if(reason != null && reason.equals(NRError.ConnectionException)) {
                    notifyConnectionError();
                } else {
                    notifyError(error, "general error: ", Color.DKGRAY);
                }
        }
    }
    
    private void notifyConnectionError() {
        ToastsKt.toast(this, "Connection failure.\nPlease check your connection.");
    }

    private void notifyConversationError(@NonNull NRError error) {
        notifyError(error, "Conversation is not available: ", Color.parseColor("#6666aa"));
    }

    private void notifyStatementError(@NonNull NRError error) {
        notifyError(error, "statement failure - ", Color.RED);
    }

    @SuppressLint("ResourceType")
    private void notifyError(@NonNull NRError error, String s, int i) {

        // this notification will not be visible

        try {List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            View snackView = fragmentList.get(fragmentList.size()-1).getView();

            if(snackView != null) {
                UtilityMethodsKt.snack(snackView,
                        s + error.getReason() + ": " + error.getDescription(),
                        4000, -1, Gravity.CENTER, new int[]{}, i);
            }
        } catch (Exception ignored) {
            ToastsKt.toast(this, s + error.getReason() + ": " + error.getDescription());
        }
    }

    @Override
    public void onUrlLinkSelected(String url) {
        // sample code for handling given link
        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Log.w(CONVERSATION_FRAGMENT_TAG, "failed to activate link on default app: " + e.getMessage());
            Toast.makeText(this, "activating: " + url, Toast.LENGTH_SHORT).show();
        }
        Log.w(CONVERSATION_FRAGMENT_TAG, "got link activation while activity is no longer available.\n(" + url + ")");
    }

    @Override
    public void onChatStateChanged(@NotNull StateEvent stateEvent) {

        Log.d(My_TAG, "onChatStateChanged: state " + stateEvent.getState() +", scope = "+stateEvent.getScope());

        switch (stateEvent.getState()) {

            case Preparing:
                //do something
                break;

            case Started:
                // display end chat button
                if (stateEvent.getScope().equals(StatementScope.BoldScope)) {
                    menu.getItem(0).setVisible(true);
                }
                break;

            case ChatWindowLoaded:
                //do something
                break;

            case Unavailable:
                toast(this, "Chat unavailable due to " + stateEvent.getData());

            case Ended:
                // hide end chat button
                if (isLive(stateEvent.getScope())) {
                    menu.getItem(0).setVisible(false);
                }
                break;

            case ChatWindowDetached:
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.popBackStack(CONVERSATION_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                break;
        }
    }

    //-> previous listener method signature @Override onPhoneNumberNavigation(@NonNull String phoneNumber) {
    @Override
    public void onPhoneNumberSelected(@NonNull String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

    // How can we change default look of chat elements UI:
    private ChatUIProvider getCustomisedChatUI() {

        ChatUIProvider uiProvider = new ChatUIProvider(this);

        uiProvider.setChatBackground(getResources().getDrawable(R.drawable.bkg_bots));

        IncomingElementUIProvider incomingUIProvider = uiProvider.getChatElementsUIProvider().getIncomingUIProvider();

        incomingUIProvider
                .setConfigure(new Function1<BubbleContentUIAdapter, BubbleContentUIAdapter>() {
                    @Override
                    public BubbleContentUIAdapter invoke(BubbleContentUIAdapter adapter) {
                        adapter.setAvatar(getResources().getDrawable(R.drawable.mr_chatbot));

                        return adapter;
                    }
                });

        incomingUIProvider
                .setCustomize(new Function2<BubbleContentUIAdapter, IncomingElementModel, BubbleContentUIAdapter>() {
                    @Override
                    public BubbleContentUIAdapter invoke(BubbleContentUIAdapter adapter, IncomingElementModel element) {
                        if (element != null) {
                            StatementScope scope = element.getElemScope();
                            if (isLive(scope)) {
                                adapter.setAvatar(getResources().getDrawable(R.drawable.bold_360));
                                adapter.setTextStyle(new StyleConfig(16, Color.DKGRAY));
                                adapter.setBackground(getResources().getDrawable(R.drawable.live_in_back));
                            }
                        }
                        return adapter;
                    }
                });

        incomingUIProvider.getCarouselUIProvider().setConfigure(new Function1<CarouselItemsUIAdapter, CarouselItemsUIAdapter>() {
            @Override
            public CarouselItemsUIAdapter invoke(CarouselItemsUIAdapter adapter) {
                adapter.setCardStyle(0F, 3F);
                adapter.setOptionsTextStyle(new StyleConfig(12, Color.BLUE));
                return adapter;
            }
        });


        incomingUIProvider.getCarouselUIProvider().setCustomize(new Function2<CarouselItemsUIAdapter, CarouselElementModel, CarouselItemsUIAdapter>() {
            @Override
            public CarouselItemsUIAdapter invoke(CarouselItemsUIAdapter adapter, CarouselElementModel element) {

                List<MultiAnswerItem> elemCarouselItems = element != null ? element.getElemCarouselItems() : null;
                int size = elemCarouselItems != null ? elemCarouselItems.size() : 0;
                if (size >= 3) {
                    adapter.setOptionsTextStyle(new StyleConfig(14, Color.RED, null));
                }
                return adapter;
            }
        });

        incomingUIProvider.getFeedbackUIProvider().setOverrideFactory(new MyFeedbackFactory());

        OutgoingElementUIProvider outgoingUIProvider = uiProvider.getChatElementsUIProvider().getOutgoingUIProvider();

        outgoingUIProvider.setConfigure(new Function1<BubbleContentUIAdapter, BubbleContentUIAdapter>() {
            @Override
            public BubbleContentUIAdapter invoke(BubbleContentUIAdapter adapter) {
                adapter.setTextStyle(new StyleConfig(18, Color.DKGRAY, Typeface.SANS_SERIF));
                adapter.setBackground(getResources().getDrawable(R.drawable.stars_back));
                adapter.setTextAlignment(UiConfigurations.Alignment.AlignCenterHorizontal, UiConfigurations.Alignment.AlignBottom);
                return adapter;
            }
        });

        outgoingUIProvider.setCustomize(new Function2<BubbleContentUIAdapter, OutgoingElementModel, BubbleContentUIAdapter>() {
            @Override
            public BubbleContentUIAdapter invoke(BubbleContentUIAdapter adapter, OutgoingElementModel element) {
                if (element != null && element.getElemContent().toLowerCase().contains("the")) {
                    adapter.setTimestampStyle(new TimestampStyle("E, HH:mm:ss", 11, Color.MAGENTA, null));
                    adapter.setTextStyle(new StyleConfig(null, Color.BLUE, Typeface.SERIF));
                    adapter.setBackground(null);
                }

                if (element != null) {
                    StatementScope scope = element.getElemScope();
                    if (isLive(scope)) {
                        adapter.setAvatar(getResources().getDrawable(R.drawable.bold_360));
                        adapter.setTextStyle(new StyleConfig(16, Color.RED));
                        adapter.setBackground(getResources().getDrawable(R.drawable.live_out_back));
                    }
                }
                return adapter;
            }
        });

        return uiProvider;
    }

    class MyFeedbackFactory implements FeedbackUIProvider.FeedbackFactory {

        @NotNull
        @Override
        public FeedbackUIAdapter create(@NotNull Context context, int feedbackDisplayType) {
            return new FeedbackViewDummy(context);
        }
    }

//<editor-fold Desc="static and internal classes">

    // *** Missing entities and personal info examples ***
    /*class MyEntitiesProvider implements EntitiesProvider {

        // handle missing entities
        @Override
        public void provide(@NonNull ArrayList<String> entities, @NonNull Completion<ArrayList<Entity>> onReady) {
            NRConversationMissingEntities missingEntities = new NRConversationMissingEntities();
            for (String missingEntity : entities) {
                if (missingEntity.equals("SUBSCRIBERS")) {
                    missingEntities.addEntity(createEntity(missingEntity));
                    break;

                }
            }

            onReady.onComplete(new ArrayList<Entity>(missingEntities.getEntities()));
        }

        // handle personal information
        @Override
        public void provide(@NotNull PersonalInfoRequest personalInfoRequest, @NotNull PersonalInfoRequest.Callback callback) {
            switch (personalInfoRequest.getId()) {
                case "balance":
                    String balance = String.format("%10.2f$", Math.random() * 10000);
                    callback.onInfoReady(balance, null);
                    return;
            }

            callback.onInfoReady("1,000$", null);
        }

        // Example for Entity creation
        private Entity createEntity(String entityName) {
            Entity entity = new Entity(Entity.PERSISTENT, Entity.NUMBER, "3", entityName, "1");
            for (int i = 0; i < 3; i++) {
                Property property = new Property(Entity.NUMBER, String.valueOf(i) + "234", "SUBSCRIBER");
                property.setName(property.getValue());
                property.addProperty(new Property(Entity.NUMBER, String.valueOf(i) + "234", "ID"));
                entity.addProperty(property);
            }
            return entity;
        }
    }
*/
    class MyAccountInfoProvider implements AccountInfoProvider {

        @Override
        public void updateAccountInfo(@NonNull AccountInfo accountInfo) {
            AccountInfo savedAccount = getAccountInfo(accountInfo.getApiKey());
            if(savedAccount != null){
                savedAccount.updateInfo(accountInfo.getInfo());
            } else {
                accounts.put(accountInfo.getApiKey(), accountInfo);
            }

        }

        @Override
        public void provide(@NonNull String apiKey, @NonNull Completion<AccountInfo> callback) {
            AccountInfo savedAccount = getAccountInfo(apiKey);

            callback.onComplete(savedAccount);
        }
    }

    static class NotificationsReceiver implements Notifiable {

        @Override
        public void onNotify(@NotNull Notification notification, @NotNull DispatchContinuation dispatcher) {
            switch (notification.getNotification()) {
                case ChatNotifications.PostChatFormSubmissionResults:
                case ChatNotifications.UnavailabilityFormSubmissionResults:
                    FormResults results = (FormResults) notification.getData();
                    if(results != null) {
                        Log.i(My_TAG, "Got notified for form results for form: " +
                                results.getData() +
                                (results.getError() != null ? (", with error: " + results.getError()):""));

                    } else {
                        Log.w(My_TAG, "Got notified for form results but results are null");
                    }
                    break;
            }
        }
    }

    static class MyFormProvider implements FormProvider{

        private final WeakReference<MainActivity> parent;

        MyFormProvider(MainActivity parent){
            this.parent = new WeakReference<MainActivity>(parent);
        }

        @Override
        public void presentForm(@NotNull FormData formData, @NonNull @NotNull FormListener callback) {
            if(parent.get() == null) {
                Log.w(My_TAG, "presentForm: form can't be presented, activity reference is lost");
                callback.onCancel(formData.getFormType());
                return;
            }

            // Demo implementation that presents present a dummy form :
            Fragment fragment = (DemoForm.create(formData, callback));

            FragmentManager fragmentManager = parent.get().getSupportFragmentManager();

            if (fragmentManager != null) {
                fragmentManager.beginTransaction()
                        .add(R.id.content_main, fragment, DEMO_FORM_TAG)
                        .addToBackStack(DEMO_FORM_TAG)
                        .commit();
            }

            Log.v("formData", "form type: " + formData.getFormType() + " form data:" + formData.logFormBrandings());

            if (formData instanceof PostChatData) {
                parent.get().menu.getItem(0).setVisible(false);
            }
        }
    }

    static class MyHistoryProvider implements HistoryProvider {

        private String accountId = null;
        final Object historySync = new Object(); // in use to block multi access to history from different actions.
        private Handler handler = null;


        MyHistoryProvider() {
            handler = new Handler(Looper.getMainLooper());
        }

        private ConcurrentHashMap<String, List<HistoryElement>> chatHistory = new ConcurrentHashMap<>();
        private boolean hasHistory = false;


        @Override
        public void fetch(final int from, @FetchDirection final int direction, final HistoryListener listener) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<? extends StorableChatElement> history;

                    synchronized (historySync) {
                        history = Collections.unmodifiableList(getHistoryForAccount(accountId, from, direction));
                    }

                    if (history.size() > 0) {
                        try {
                            Thread.sleep(800); // simulate async history fetching
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (handler.getLooper()!=null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("History", "passing history list to listener, from = "+from + ", size = "+history.size());
                                hasHistory = history.size() > 0;
                                listener.onReady(from, direction, history);
                            }
                        });
                    }
                }
            }).start();
        }

        @Override
        public void store(@NonNull StorableChatElement item) {
            //if(item == null || item.getStatus() != StatusOk) return;

            synchronized (historySync) {
                ArrayList<HistoryElement> convHistory = getAccountHistory(accountId);
                convHistory.add(new HistoryElement(item));
            }
        }

        @Override
        public void remove(long timestampId) {
            synchronized (historySync) {
                ArrayList<HistoryElement> convHistory = getAccountHistory(accountId);

                Iterator<HistoryElement> iterator = convHistory.listIterator();
                while (iterator.hasNext()) {
                    HistoryElement item = iterator.next();
                    if (item.getTimestamp() == timestampId) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }

        @Override
        public void update(long timestampId, @NotNull StorableChatElement item) {
            synchronized (historySync) {
                ArrayList<HistoryElement> convHistory = getAccountHistory(accountId);
                for (int i = convHistory.size() - 1; i >= 0; i--) {
                    if (convHistory.get(i).getTimestamp() == timestampId) {
                        convHistory.set(i, new HistoryElement(item));
                        break;
                    }
                }
            }
        }

        @NonNull
        private ArrayList<HistoryElement> getAccountHistory(String accountId) {
            ArrayList<HistoryElement> convHistory;
            if(chatHistory.containsKey(accountId)) {
                convHistory = (ArrayList<HistoryElement>) chatHistory.get(accountId);
            } else {
                convHistory = new ArrayList<>();
                chatHistory.put(accountId,  convHistory);
            }
            return convHistory;
        }

        private List<HistoryElement> getHistoryForAccount(String account, int fromIdx, int direction) {

            List<HistoryElement> accountChatHistory = chatHistory.get(account);

            if(accountChatHistory == null)
                return new ArrayList<>();

            boolean fetchOlder = direction == Older;

            // to prevent Concurrent exception
            CopyOnWriteArrayList<HistoryElement> accountHistory = new CopyOnWriteArrayList<>(accountChatHistory);

            int historySize = accountHistory.size();

            if(fromIdx == -1) {
                fromIdx = fetchOlder ? historySize - 1: 0;
            } else if(fetchOlder){
                fromIdx = historySize - fromIdx;
            }

            int toIndex = fetchOlder ? Math.max(0, fromIdx-HistoryPageSize) :
                    Math.min(fromIdx+HistoryPageSize, historySize-1);

            try {
                Log.d("History", "fetching history items ("+ historySize +") from " +toIndex+ " to "+fromIdx);

                return accountHistory.subList(toIndex, fromIdx);

            } catch (Exception ex) {
                return new ArrayList<>();
            }
        }

    }

    /**
     * {@link StorableChatElement} implementing class
     * sample class for app usage
     */
    static class HistoryElement implements StorableChatElement {
        byte[] key;
        protected long timestamp = 0;
        StatementScope scope;
        protected @ChatElement.Companion.ChatElementType
        int type;
        protected @StatementStatus
        int status = StatusPending;

        HistoryElement(int type, long timestamp) {
            this.type = type;
            this.timestamp = timestamp;
        }

        HistoryElement(StorableChatElement storable) {
            key = storable.getStorageKey();
            type = storable.getType();
            timestamp = storable.getTimestamp();
            status = storable.getStatus();
            scope = storable.getScope();
        }

        @NonNull
        @Override
        public byte[] getStorageKey() {
            return key;
        }

        @NonNull
        @Override
        public String getStorableContent() {
            return new String(key);
        }

        @Override
        public int getType() {
            return type;
        }

        @Override
        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @NotNull
        @Override
        public StatementScope getScope() {
            return scope;
        }

        @Override
        public boolean isStorageReady() {
            return true;
        }
    }

//</editor-fold>
}

