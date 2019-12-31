/*
package com.sdk.samples.topics.handover;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.integration.core.ErrorEvent;
import com.integration.core.MessageEvent;
import com.integration.core.StateEvent;
import com.integration.core.UserEvent;
import com.nanorep.convesationui.structure.HandoverHandler;
import com.nanorep.convesationui.structure.UiConfigurations;
import com.nanorep.convesationui.structure.components.ComponentType;
import com.nanorep.convesationui.views.autocomplete.ChatInputData;
import com.nanorep.nanoengine.AccountInfo;
import com.nanorep.nanoengine.model.configuration.ChatFeatures;
import com.nanorep.nanoengine.model.conversation.statement.IncomingStatement;
import com.nanorep.nanoengine.model.conversation.statement.OutgoingStatement;
import com.nanorep.sdkcore.model.ChatStatement;
import com.nanorep.sdkcore.utils.Event;
import com.nanorep.sdkcore.utils.NRError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

import static com.integration.core.EventsKt.Error;
import static com.integration.core.EventsKt.Message;
import static com.integration.core.EventsKt.State;
import static com.integration.core.EventsKt.UserAction;
import static com.integration.core.StateEvent.Ended;
import static com.integration.core.StateEvent.Resumed;
import static com.integration.core.StateEvent.Started;
import static com.integration.core.UserEvent.ActionLink;
import static com.nanorep.nanoengine.model.conversation.SessionInfoKt.getProviderConfig;
import static com.nanorep.sdkcore.model.StatementModels.StatusOk;
import static com.nanorep.sdkcore.model.StatementModels.StatusPending;
import static com.nanorep.sdkcore.utils.UtilityMethodsKt.getAs;

public class MyHandoverHandler extends HandoverHandler {

    private static int responseNumber = 1;

    private String handlerConfiguration;
    private Handler handler = new Handler();

    public MyHandoverHandler(@NotNull Context context) {
        super(context);
    }

    @Override
    public void handleEvent(@NotNull String name, @NotNull Event event) {
        switch (name) {

            case UserAction:
                if (event instanceof UserEvent) {
                    UserEvent userEvent = (UserEvent) event;
                    if (userEvent.getAction().equals(ActionLink)) {
                        passEvent(userEvent);
                    }
                }
                break;

            case Message:
                ChatStatement statement = getAs(event.getData());
                if (statement != null) {
                    injectElement(statement);
                }
                break;

            case State:
                handleState(getAs(event));
                break;

            case Error:
                handleError(event);
                break;

            default:
                passEvent(event);
                break;
        }

        // If there is any post event function, invoke it after the event handling
        Function0 postEvent = event.getPostEvent();
        if (postEvent != null) {
            postEvent.invoke();
        }
    }

    private void handleError(@NotNull Event event) {
        if (event instanceof ErrorEvent) {
            ErrorEvent errorEvent = (ErrorEvent) event;

            if (errorEvent.getCode().equals(NRError.StatementError)) {
                ChatStatement request = getAs(errorEvent.getData());
                if (request == null) {
                    NRError error = getAs(errorEvent.getData());
                    if (error != null) {
                        request = getAs(error.getData());
                    }
                }

                if (request != null) {
                    updateStatus(request, StatusPending);
                }
            }
        }

        passEvent(event);
    }

    @Override
    protected void enableChatInput(boolean enable, @Nullable ChatInputData cmpData) {
        cmpData = new ChatInputData();
        cmpData.setOnSend(enable ? new Function1<CharSequence, Unit>() {
            @Override
            public Unit invoke(CharSequence charSequence) {
                post(new OutgoingStatement(charSequence.toString()));
                return null;
            }
        } : null);
        cmpData.setVoiceEnabled(enable && UiConfigurations.FeaturesDefaults.isEnabled(ChatFeatures.SpeechRecognition));
        cmpData.setInputEnabled(enable);

        super.enableChatInput(enable, cmpData);
    }

    @Override
    public void startChat(@Nullable AccountInfo accountInfo) {

        this.enableChatInput(true, null);

        if (accountInfo != null) {
            handlerConfiguration = getProviderConfig(accountInfo.getInfo());
        }

        handleEvent(State, new StateEvent(Started, getScope()));

        setChatStarted(true);
    }

    @Override
    public void endChat(boolean forceClose) {
        handleEvent(State, new StateEvent(Ended, getScope()));

        enableChatInput(false, null);

        setChatStarted(false);
    }

    @Override
    public void post(@NotNull ChatStatement message) {
        injectElement(message);
        updateStatus(message, StatusOk);
        simulateAgentResponse(message.getText());
    }

    @Override
    protected void handleState(StateEvent event) {

        switch (event.getState()) {
            case Started:
                Log.e("MainFragment", "started handover");

                injectSystemMessage("Started Chat with Handover provider, the handover data is: " + handlerConfiguration);
                injectElement(new IncomingStatement("Hi from handover", getScope()));

                passStateEvent(event);
                break;

            case Ended:
                Log.e("MainFragment", "handover ended");

                injectElement(new IncomingStatement("bye from handover", getScope()));
                injectSystemMessage("Ended Chat with the Handover provider");

                passStateEvent(event);
                break;

            case Resumed:
                onResume();
                break;

            default:
                super.handleState(event);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        enableChatInput(true, null);
    }

    */
/***
     * A function used to simulate agent typing indication
     * @param outgoingMessage
     *//*

    private void simulateAgentResponse(String outgoingMessage) {

        presentTypingIndication(true);

        Runnable runnable = () -> {

            String agentAnswer = "handover response number " + responseNumber++;

            if (outgoingMessage.toLowerCase().equals("url test")) {
                agentAnswer = "<a href=\"https://www.google.com\">Google link</a>";
            }

            presentTypingIndication(false);

            // Event to be sent after the agent response:
            handleEvent(Message, new MessageEvent(new IncomingStatement(agentAnswer, getScope())));

        };

        handler.postDelayed(runnable, 2000);
    }

    private void presentTypingIndication(boolean isTyping) {
        */
/* -> In order to use the apps custom typing indication use:
            listener.handleEvent(Operator, new OperatorEvent(OperatorEvent.OperatorTyping, getScope(), isTyping));
         *//*


        Log.d("handover", "event: operatorTyping: " + isTyping);
        if (getChatDelegate() == null) return;

        if (isTyping) {
            getChatDelegate().updateCmp(ComponentType.LiveTypingCmp, null);
        } else {
            getChatDelegate().removeCmp(ComponentType.LiveTypingCmp, true);
        }
    }
}
*/
