package com.common.utils.forms.defs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({DataKeys.Name, DataKeys.KB, DataKeys.Server, DataKeys.Accesskey})

public @interface DataKeys {
    String Name = "account";
    String KB = "kb";
    String Server = "domain";
    String Accesskey = "accessKey";
}