package com.common.utils.loginForms.accountUtils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({FormsParams.Welcome, FormsParams.PrechatExtraData, FormsParams.AsyncExtraData,
        FormsParams.EnableRestore, FormsParams.UsingContext})

public @interface FormsParams {

    int Welcome = 0x12;
    int PrechatExtraData = 0x16;
    int AsyncExtraData = 0x22;
    int EnableRestore = 0x23;
    int UsingContext = 0x24;
}