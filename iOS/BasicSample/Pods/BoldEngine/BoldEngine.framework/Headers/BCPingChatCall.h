
// NanorepUI version number: v2.3.6.rc2 

//
//  BCPingChatCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/14/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCPingChatCallResult.h"
#import "BCCall.h"

@class BCPingChatCall;

@protocol BCPingChatCallDelegate <NSObject>
- (void)bcPingChatCall:(BCPingChatCall *)pingChatCall didFinishWithResult:(BCPingChatCallResult *)result;
- (void)bcPingChatCall:(BCPingChatCall *)pingChatCall didFinishWithError:(NSError *)error;
@end

@interface BCPingChatCall : BCCall

@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, assign)BOOL closed;
@property(nonatomic, assign)id<BCPingChatCallDelegate> delegate;

@end
