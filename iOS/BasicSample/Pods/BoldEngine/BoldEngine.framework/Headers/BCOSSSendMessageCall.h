
// NanorepUI version number: v2.3.6.rc2 

//
//  BCOSSSendMessageCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/1/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import "BCOSSCall.h"

@class BCOSSSendMessageCall;

@protocol BCOSSSendMessageCallDelegate <NSObject>
- (void)ossSendMessageCallDidSucceed:(BCOSSSendMessageCall *)ossSendMessageCall;
@end


@interface BCOSSSendMessageCall : BCOSSCall

@property(nonatomic, assign)id<BCOSSSendMessageCallDelegate> delegate;
@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, strong)NSString *chatMessageID;
@property(nonatomic, strong)NSString *name;
@property(nonatomic, strong)NSString *message;

@end
