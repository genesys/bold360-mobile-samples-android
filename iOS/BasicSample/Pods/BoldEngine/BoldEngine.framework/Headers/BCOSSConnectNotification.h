
// NanorepUI version number: v2.3.6.rc2 

//
//  BCOSSConnectNotification.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/31/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import "BCOSSNotification.h"

@class BCOSSConnectNotification;

@protocol BCOSSConnectNotificationDelegate <NSObject>
- (void)ossConnectNotificationDidConnect:(BCOSSConnectNotification *)notification;
- (void)ossConnectNotification:(BCOSSConnectNotification *)notification didRedirectToUrl:(NSString *)redirectUrl;
- (void)ossConnectNotificationDidReconnect:(BCOSSConnectNotification *)notification;
- (void)ossConnectNotificationDidReset:(BCOSSConnectNotification *)notification;
- (void)ossConnectNotificationDidClose:(BCOSSConnectNotification *)notification;
@end

@interface BCOSSConnectNotification : BCOSSNotification

@property(nonatomic, assign)id<BCOSSConnectNotificationDelegate> delegate;

@end
