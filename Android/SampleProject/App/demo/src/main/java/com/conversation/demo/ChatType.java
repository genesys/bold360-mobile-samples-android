package com.conversation.demo;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.conversation.demo.ChatType.BotChat;
import static com.conversation.demo.ChatType.LiveChat;

@Retention(RetentionPolicy.SOURCE)
@StringDef({LiveChat, BotChat})
public @interface ChatType {
    String LiveChat = "live";
    String BotChat = "bot";
}
