
// NanorepUI version number: v2.3.6.rc2 

//
//  BCVisitorTypingCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/2/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCVisitorTypingCallResult.h"
#import "BCCall.h"

@class BCVisitorTypingCall;

@protocol BCVisitorTypingCallDelegate <NSObject>
- (void)bcVisitorTypingCall:(BCVisitorTypingCall *)visitorTypingCall didFinishWithResult:(BCVisitorTypingCallResult *)result;
- (void)bcVisitorTypingCall:(BCVisitorTypingCall *)visitorTypingCall didFinishWithError:(NSError *)error;
@end


@interface BCVisitorTypingCall : BCCall

@property(nonatomic, copy)NSString *chatKey;
@property(nonatomic, assign)BOOL isTyping;
@property(nonatomic, assign)id<BCVisitorTypingCallDelegate> delegate;

@end
