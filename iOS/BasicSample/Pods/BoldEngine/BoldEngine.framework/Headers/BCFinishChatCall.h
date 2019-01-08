
// NanorepUI version number: v2.3.6.rc2 

//
//  BCFinishChatCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/2/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCFinishChatCallResult.h"
#import "BCCall.h"


@class BCFinishChatCall;

@protocol BCFinishChatCallDelegate <NSObject>
- (void)bcFinishChatCall:(BCFinishChatCall *)finishMessageCall didFinishWithResult:(BCFinishChatCallResult *)result;
- (void)bcFinishChatCall:(BCFinishChatCall *)finishMessageCall didFinishWithError:(NSError *)error;
@end


@interface BCFinishChatCall : BCCall
@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, strong)NSString *clientId;
@property(nonatomic, assign)id<BCFinishChatCallDelegate> delegate;

@end
