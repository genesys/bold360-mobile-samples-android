
// NanorepUI version number: v2.3.6.rc2 

//
//  BCSendMessageCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/2/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCSendMessageCallResult.h"
#import "BCCall.h"

@class BCSendMessageCall;

@protocol BCSendMessageCallDelegate <NSObject>
- (void)bcSendMessageCall:(BCSendMessageCall *)sendMessageCall didFinishWithResult:(BCSendMessageCallResult *)result;
- (void)bcSendMessageCall:(BCSendMessageCall *)sendMessageCall didFinishWithError:(NSError *)error;
@end


@interface BCSendMessageCall : BCCall

@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, strong)NSString *chatMessageID;
@property(nonatomic, strong)NSString *name;
@property(nonatomic, strong)NSString *message;
@property(nonatomic, assign)id<BCSendMessageCallDelegate> delegate;

@end
