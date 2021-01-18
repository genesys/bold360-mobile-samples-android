package com.common.utils.loginForms.dynamicFormPOC.defs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({FieldKeys.Name, FieldKeys.KB, FieldKeys.Server, FieldKeys.AccessKey})

public @interface FieldKeys {

    String Name = "accountName";
    String KB = "kb";
    String Server = "server";
    String AccessKey = "apiKey";
}