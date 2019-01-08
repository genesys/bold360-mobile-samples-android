
// NanorepUI version number: v2.3.6.rc2 

//
//  BCOSSFinishChatCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/1/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import "BCOSSCall.h"

@class BCOSSFinishChatCall;

@protocol BCOSSFinishChatCallDelegate <NSObject>
- (void)ossFinishChatCallDidSucceed:(BCOSSFinishChatCall *)ossFinishChatCall;
@end


@interface BCOSSFinishChatCall : BCOSSCall

@property(nonatomic, assign)id<BCOSSFinishChatCallDelegate> delegate;
@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, strong)NSString *clientId;
@end
