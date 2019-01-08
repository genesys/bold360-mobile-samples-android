
// NanorepUI version number: v2.3.6.rc2 

//
//  BCCreateChatCallResult.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/28/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCRESTCallResult.h"

@interface BCCreateChatCallResult : BCRESTCallResult

@property(nonatomic, strong)NSString *chatId;
@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, strong)NSString *visitorId;
@property(nonatomic, strong)NSString *name;
@property(nonatomic, strong)NSString *clientId;
@property(nonatomic, strong)NSString *longPollURL;
@property(nonatomic, strong)NSString *webSocketURL;
@property(nonatomic, strong)NSString *language;
@property(nonatomic, assign)NSInteger clientTimeout;
@property(nonatomic, copy)NSArray *unavailableForm;
@property(nonatomic, copy)NSArray *preChat;
@property(nonatomic, copy)NSDictionary *brandings;
@property(nonatomic, strong)NSString *unavailableReason;
@property(nonatomic, assign)NSInteger answerTimeout;

@end
