package com.common.utils.forms.defs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({FieldTypes.Title, FieldTypes.TextInput, FieldTypes.RestoreBlock})

public @interface FieldTypes {
    String Title  = "title";
    String TextInput  = "textInput";
    String RestoreBlock  = "restoreBlock";
}