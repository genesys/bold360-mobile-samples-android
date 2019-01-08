
// NanorepUI version number: v2.3.6.rc2 

//
//  BCGetChatAvailabilityCallResult.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/9/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCRESTCallResult.h"

@interface BCGetChatAvailabilityCallResult : BCRESTCallResult
@property(nonatomic, assign)BOOL available;
@property(nonatomic, strong)NSString *unavailableReason;

@end
