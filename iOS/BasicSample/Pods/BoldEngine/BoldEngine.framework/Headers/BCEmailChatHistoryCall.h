
// NanorepUI version number: v2.3.6.rc2 

//
//  BCEmailChatHistoryCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/10/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCEmailChatHistoryCallResult.h"
#import "BCCall.h"

@class BCEmailChatHistoryCall;

@protocol BCEmailChatHistoryCallDelegate <NSObject>
- (void)bcEmailChatHistoryCall:(BCEmailChatHistoryCall *)emailChatHistoryCall didFinishWithResult:(BCEmailChatHistoryCallResult *)result;
- (void)bcEmailChatHistoryCall:(BCEmailChatHistoryCall *)emailChatHistoryCall didFinishWithError:(NSError *)error;
@end

@interface BCEmailChatHistoryCall : BCCall

@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, strong)NSString *emailAddress;
@property(nonatomic, assign)id<BCEmailChatHistoryCallDelegate> delegate;

@end
