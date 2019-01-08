
// NanorepUI version number: v2.3.6.rc2 

//
//  BCOSSUpdateChatNotification.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/31/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import "BCOSSNotification.h"

@class BCOSSUpdateChatNotification;

@protocol BCOSSUpdateChatNotificationDelegate <NSObject>
- (void)ossUpdateChatNotification:(BCOSSUpdateChatNotification *)notification chatId:(NSString *)chatId
                         answered:(NSString *)answered endedAt:(NSDate *)endTime reason:(NSString *)reason;
@end

@interface BCOSSUpdateChatNotification : BCOSSNotification

@property(nonatomic, assign)id<BCOSSUpdateChatNotificationDelegate> delegate;

@end
