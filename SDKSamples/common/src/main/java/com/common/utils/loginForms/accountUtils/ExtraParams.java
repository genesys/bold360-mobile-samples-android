package com.common.utils.loginForms.accountUtils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ExtraParams.Welcome, ExtraParams.PrechatExtraData, ExtraParams.AsyncExtraData,
        ExtraParams.EnableRestore, ExtraParams.UsingContext})

public @interface ExtraParams {
    String Welcome = "welcome";
    String PrechatExtraData = "PrechatExtraData";
    String AsyncExtraData = "AsyncExtraData";
    String EnableRestore = "RestoreSwitch";
    String UsingContext = "UsingContext";
}