package com.common.utils.loginForms.accountUtils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ExtraParams.Welcome, ExtraParams.PrechatExtraData, ExtraParams.AsyncExtraData,
        ExtraParams.EnableRestore, ExtraParams.UsingContext})

public @interface ExtraParams {

    int Welcome = 0x12;
    int PrechatExtraData = 0x21;
    int AsyncExtraData = 0x22;
    int EnableRestore = 0x33;
    int UsingContext = 0x66;
}