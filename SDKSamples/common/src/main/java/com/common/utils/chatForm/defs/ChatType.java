package com.common.utils.chatForm.defs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ChatType.Live, ChatType.Bot, ChatType.Async, ChatType.ChatSelection, ChatType.ContinueLast})
public @interface ChatType {
    String ChatSelection = "Chat selection";
    String Live = "Live Chat";
    String Async = "Async Chat";
    String Bot = "Bot Chat";
    String ContinueLast = "Continue Last";
}
