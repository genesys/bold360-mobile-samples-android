
// NanorepUI version number: v2.3.6.rc2 

//
//  BCOSSVisitorTypingCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/1/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import "BCOSSCall.h"

@class BCOSSVisitorTypingCall;

@protocol BCOSSVisitorTypingCallDelegate <NSObject>
- (void)ossVisitorTypingCallDidSucceed:(BCOSSVisitorTypingCall *)visitorTypingCall;
@end

@interface BCOSSVisitorTypingCall : BCOSSCall

@property(nonatomic, assign)id<BCOSSVisitorTypingCallDelegate> delegate;
@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, assign)BOOL typing;

@end
