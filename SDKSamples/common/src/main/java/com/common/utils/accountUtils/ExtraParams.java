package com.common.utils.accountUtils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ExtraParams.Welcome, ExtraParams.PrechatExtraData, ExtraParams.AsyncExtraData, ExtraParams.RestoreSwitch})
public @interface ExtraParams {
    String Welcome = "welcome";
    String PrechatExtraData = "PrechatExtraData";
    String AsyncExtraData = "AsyncExtraData";
    String RestoreSwitch = "RestoreSwitch";
    String UsingHistory = "UsingHistory";
}