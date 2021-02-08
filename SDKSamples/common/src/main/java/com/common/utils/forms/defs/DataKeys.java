package com.common.utils.forms.defs;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({DataKeys.AccountName, DataKeys.KB, DataKeys.Server, DataKeys.Accesskey,
        DataKeys.FirstName, DataKeys.LastName, DataKeys.Email, DataKeys.CountryAbbrev,
        DataKeys.AppId, DataKeys.PhoneNumber, DataKeys.UserId, DataKeys.preChat_fName,
        DataKeys.preChat_lName, DataKeys.preChat_deptCode, DataKeys.Restore})

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
    String Restore = "Restore if available";
    String preChat_fName = "pre_firstName";
    String preChat_lName = "pre_lastName";
    String preChat_deptCode = "pre_deptCode";

}