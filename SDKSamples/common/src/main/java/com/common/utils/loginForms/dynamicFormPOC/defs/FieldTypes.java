package com.common.utils.loginForms.dynamicFormPOC.defs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({FieldTypes.Title, FieldTypes.TextInput, FieldTypes.Radio})

public @interface FieldTypes {
    String Title  = "title";
    String TextInput  = "textInput";
    String Radio  = "radio";
}