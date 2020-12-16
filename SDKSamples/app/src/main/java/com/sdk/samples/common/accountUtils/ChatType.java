package com.sdk.samples.common.accountUtils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ChatType.Live, ChatType.Bot, ChatType.Async, ChatType.None})
public @interface ChatType {
    String Live = "live";
    String Async = "async";
    String Bot = "bot";
    String None = "none";
}
