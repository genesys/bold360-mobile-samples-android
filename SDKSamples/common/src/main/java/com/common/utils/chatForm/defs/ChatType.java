package com.common.utils.chatForm.defs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ChatType.Live, ChatType.Bot, ChatType.Async, ChatType.None, ChatType.ContinueLast})
public @interface ChatType {
    String Live = "Live Chat";
    String Async = "Async Chat";
    String Bot = "Bot Chat";
    String ContinueLast = "Continue Last";
    String None = "none";
}
