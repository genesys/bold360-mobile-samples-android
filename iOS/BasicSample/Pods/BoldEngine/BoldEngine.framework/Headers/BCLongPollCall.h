
// NanorepUI version number: v2.3.6.rc2 

//
//  BCLongPollCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/4/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCCall.h"


@class BCLongPollCall;

@protocol BCLongPollCallDelegate <NSObject>
- (void)bcLongPollCall:(BCLongPollCall *)longPollCall didFinishWithResult:(NSArray *)result;
- (void)bcLongPollCall:(BCLongPollCall *)longPollCall didFinishWithError:(NSError *)error;
@end

@interface BCLongPollCall : BCCall

@property(nonatomic, strong)NSString *url;
@property(nonatomic, assign)long long lastMessageId;
@property(nonatomic, assign)id<BCLongPollCallDelegate> delegate;

@end
