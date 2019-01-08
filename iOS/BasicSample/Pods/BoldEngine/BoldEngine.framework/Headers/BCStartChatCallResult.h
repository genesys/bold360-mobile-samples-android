
// NanorepUI version number: v2.3.6.rc2 

//
//  BCStartChatCallResult.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/28/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCRESTCallResult.h"

@interface BCStartChatCallResult : BCRESTCallResult

@property(nonatomic, strong)NSString *clientId;
@property(nonatomic, strong)NSString *longPollURL;
@property(nonatomic, strong)NSString *webSocketURL;
@property(nonatomic, assign)NSInteger clientTimeout;
@property(nonatomic, assign)NSInteger answerTimeout;

@end
