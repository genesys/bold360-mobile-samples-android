
// NanorepUI version number: v2.3.6.rc2 

//
//  BCSubmitPreChatCallResult.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/10/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCRESTCallResult.h"

@interface BCSubmitPreChatCallResult : BCRESTCallResult

@property(nonatomic, strong)NSString *clientId;
@property(nonatomic, strong)NSString *name;
@property(nonatomic, strong)NSString *longPollURL;
@property(nonatomic, strong)NSString *webSocketURL;
@property(nonatomic, assign)NSInteger clientTimeout;
@property(nonatomic, copy)NSArray *unavailableForm;
@property(nonatomic, strong)NSString *unavailableReason;
@property(nonatomic, assign)NSInteger answerTimeout;

@end
