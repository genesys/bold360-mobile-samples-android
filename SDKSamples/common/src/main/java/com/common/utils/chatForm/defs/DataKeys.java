package com.common.utils.chatForm.defs;

import androidx.annotation.StringDef;

import com.nanorep.nanoengine.model.conversation.SessionInfoKeys;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({DataKeys.AccountName, DataKeys.KB, DataKeys.Server, DataKeys.Accesskey,
        DataKeys.Email, DataKeys.CountryAbbrev,DataKeys.ChatTypeKey,
        DataKeys.AppId, DataKeys.Phone, DataKeys.UserId, DataKeys.LastName,
        DataKeys.FirstName, DataKeys.Department, DataKeys.Restore})

public @interface DataKeys {
    String AccountName = "account";
    String KB = "kb";
    String Server = "domain";
    String Accesskey = "apiKey";
    String Info = "info";
    String Context = "context";
    String Welcome = "welcome";
    String AppId = "applicationId";
    String UserId = "id";
    String Restore = "Restore if available";
    String ChatTypeKey = "chatType";
    String FirstName = SessionInfoKeys.FirstName;
    String LastName = SessionInfoKeys.LastName;
    String Email = SessionInfoKeys.Email;
    String CountryAbbrev = SessionInfoKeys.countryAbbrev;
    String Phone = SessionInfoKeys.Phone;
    String Department = SessionInfoKeys.Department;

}