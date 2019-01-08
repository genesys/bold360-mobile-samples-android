
// NanorepUI version number: v2.3.6.rc2 

//
//  BCGetChatAvailabilityCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/9/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCGetChatAvailabilityCallResult.h"
#import "BCCall.h"

@class BCGetChatAvailabilityCall;

@protocol BCGetChatAvailabilityCallDelegate <NSObject>
- (void)bcGetChatAvailabilityCall:(BCGetChatAvailabilityCall *)getChatAvailabilityCall didFinishWithResult:(BCGetChatAvailabilityCallResult *)result;
- (void)bcGetChatAvailabilityCall:(BCGetChatAvailabilityCall *)getChatAvailabilityCall didFinishWithError:(NSError *)error;
@end

@interface BCGetChatAvailabilityCall : BCCall

@property(nonatomic, strong)NSString *visitorId;
@property(nonatomic, assign)id<BCGetChatAvailabilityCallDelegate> delegate;

@end
