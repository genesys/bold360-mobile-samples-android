
// NanorepUI version number: v2.3.6.rc2 

//
//  BCOSSAddMessageNotification.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/31/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import "BCOSSNotification.h"

@class BCOSSAddMessageNotification;
@class BCMessage;

@protocol BCOSSAddMessageNotificationDelegate <NSObject>

- (void)ossAddMessageNotification:(BCOSSAddMessageNotification *)addMessageNotification didReceiveMessage:(BCMessage *)message;

@end

@interface BCOSSAddMessageNotification : BCOSSNotification
@property(nonatomic, assign)id<BCOSSAddMessageNotificationDelegate> delegate;
@end
