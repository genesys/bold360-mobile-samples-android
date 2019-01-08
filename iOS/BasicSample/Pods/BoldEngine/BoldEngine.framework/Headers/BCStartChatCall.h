
// NanorepUI version number: v2.3.6.rc2 

//
//  BCStartChatCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/28/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCCall.h"
#import "BCStartChatCallResult.h"

@class BCStartChatCall;

@protocol BCStartChatCallDelegate <NSObject>
- (void)bcStartChatCall:(BCStartChatCall *)startChatCall didFinishWithResult:(BCStartChatCallResult *)result;
- (void)bcStartChatCall:(BCStartChatCall *)startChatCall didFinishWithError:(NSError *)error;
@end

@interface BCStartChatCall : BCCall

@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, assign)long long lastChatMessageId;
@property(nonatomic, assign)id<BCStartChatCallDelegate> delegate;

@end
