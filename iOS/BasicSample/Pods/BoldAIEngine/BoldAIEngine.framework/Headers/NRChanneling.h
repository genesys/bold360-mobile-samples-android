
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, NRChannelingType) {
    NRChannelingTypeCustomScript,
    NRChannelingTypeContactForm,
    NRChannelingTypeOpenCustomURL,
    NRChannelingTypeChatForm,
    NRChannelingTypePhoneNumber = 5
};

@interface NRChanneling : NSObject
- (instancetype)initWithParams:(NSDictionary *)params;
@property (nonatomic, copy, readonly) NSString *buttonText;
@property (nonatomic, copy, readonly) NSString *channelDescription;
@property (nonatomic, copy, readonly) NSString *icon;
@property (nonatomic, copy, readonly) NSString *name;
@property (nonatomic, readonly) NRChannelingType type;
@property (nonatomic, copy, readonly) NSDictionary *params;
@end

@interface NRChannelingPhoneNumber : NRChanneling
@property (nonatomic, copy, readonly) NSString *phoneNumber;
@property (nonatomic, copy, readonly) NSString *customContent;
@end

@interface NRChannelingOpenCustomURL : NRChanneling
@property (nonatomic, copy, readonly) NSString *linkUrl;
@property (nonatomic, copy, readonly) NSString *linkTarget;
@property (nonatomic, copy, readonly) NSString *popupSize;
@end

@interface NRChannelingCustomScript : NRChanneling
@property (nonatomic, copy, readonly) NSString *scriptContent;
@end

@interface NRChannelingContactForm : NRChanneling
@property (nonatomic, copy, readonly) NSString *contactForms;
@property (nonatomic, copy, readonly) NSString *ticketingInterface;
@property (nonatomic, copy, readonly) NSString *showInArticle;
@property (nonatomic, copy, readonly) NSString *thankYouMessage;
@end

@interface NRChannelingChatForm : NRChanneling
@property (nonatomic, copy, readonly) NSString *chatProvider;
@property (nonatomic, copy, readonly) NSString *accountNum;
@property (nonatomic, copy, readonly) NSString *initialiseStatus;
@property (nonatomic, copy, readonly) NSString *agentSkill;
@property (nonatomic, copy, readonly) NSString *waitTime;
@property (nonatomic, copy, readonly) NSString *preChat;
@property (nonatomic, copy, readonly) NSString *postChat;
@property (nonatomic, readonly) BOOL hideSendToEmail;
@property (nonatomic, readonly) BOOL isPopup;
@property (nonatomic, copy, readonly) NSString *popupSize;
@property (nonatomic, copy, readonly) NSString *otherChatProviderValues;
@property (nonatomic, copy, readonly) NSString *accontNum;
@property (nonatomic, copy, readonly) NSString *apiKey;
@property (nonatomic, copy, readonly) NSDictionary *scriptContent;
@end
