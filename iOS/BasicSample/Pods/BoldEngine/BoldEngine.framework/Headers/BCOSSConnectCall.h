
// NanorepUI version number: v2.3.6.rc2 

//
//  BCOSSConnectCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/31/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import "BCOSSCall.h"

@class BCOSSConnectCall;

@protocol BCOSSConnectCallDelegate <NSObject>

- (void)ossConnectCallDidSucceed:(BCOSSConnectCall *)connectCall;

@end

@interface BCOSSConnectCall : BCOSSCall

@property(assign)long long lastMessageId;
@property(assign)id<BCOSSConnectCallDelegate> delegate;

@end
