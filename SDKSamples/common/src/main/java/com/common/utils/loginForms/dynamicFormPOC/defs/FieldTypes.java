package com.common.utils.loginForms.dynamicFormPOC.defs;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({FieldTypes.Title, FieldTypes.TextInput, FieldTypes.Radio})

public @interface FieldTypes {

    int Title  = 1;
    int TextInput  = 2;
    int Radio  = 3;
}