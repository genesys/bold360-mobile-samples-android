package com.common.utils.chatForm.defs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({DataKeys.AccountName, DataKeys.KB, DataKeys.Server, DataKeys.Accesskey,
        DataKeys.FirstName, DataKeys.LastName, DataKeys.Email, DataKeys.CountryAbbrev,
        DataKeys.AppId, DataKeys.PhoneNumber, DataKeys.UserId, DataKeys.PreChat_fName,
        DataKeys.PreChat_lName, DataKeys.PreChat_deptCode, DataKeys.Restore})

public @interface DataKeys {
    String AccountName = "account";
    String KB = "kb";
    String Server = "domain";
    String Accesskey = "apiKey";
    String Info = "info";
    String Context = "context";
    String Welcome = "welcome";
    String FirstName = "first_name";
    String LastName = "last_name";
    String Email = "email";
    String CountryAbbrev = "country_abbrev";
    String AppId = "applicationId";
    String PhoneNumber = "phone_number";
    String UserId = "id";
    String PreChat_fName = "pre_firstName";
    String PreChat_lName = "pre_lastName";
    String PreChat_deptCode = "pre_deptCode";
    String ChatTypeKey = "chatType";
    String Restore = "Restore if available";

}