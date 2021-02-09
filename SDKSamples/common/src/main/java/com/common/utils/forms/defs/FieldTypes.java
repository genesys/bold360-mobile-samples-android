package com.common.utils.forms.defs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ FieldTypes.Title, FieldTypes.TextInput, FieldTypes.Switch, FieldTypes.ContextView, FieldTypes.RadioOption })

public @interface FieldTypes {
    String Title  = "title";
    String TextInput  = "textInput";
    String RadioOption  = "radioOption";
    String Switch = "switch";
    String ContextView = "context";
}