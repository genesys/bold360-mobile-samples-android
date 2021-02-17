package com.common.utils.chatForm.defs;

import androidx.annotation.StringDef;

import com.nanorep.nanoengine.model.conversation.SessionInfoKeys;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({DataKeys.AccountName, DataKeys.KB, DataKeys.Server, DataKeys.Accesskey,
        SessionInfoKeys.Email, SessionInfoKeys.countryAbbrev,
        DataKeys.AppId, SessionInfoKeys.Phone, DataKeys.UserId, SessionInfoKeys.LastName,
        SessionInfoKeys.FirstName, SessionInfoKeys.Department, DataKeys.Restore})

public @interface DataKeys {
    String AccountName = "account";
    String KB = "kb";
    String Server = "domain";
    String Accesskey = "apiKey";
    String Info = "info";
    String Context = "context";
    String Welcome = "welcome";
    String UserId = "id";
    String AppId = "applicationId";
    String ChatTypeKey = "chatType";
    String Restore = "Restore if available";

}