package com.common.utils.chatForm.defs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({FormType.Restoration, FormType.Account})
public @interface FormType {
    String Restoration = "restore_form";
    String Account = "account_form";
}
