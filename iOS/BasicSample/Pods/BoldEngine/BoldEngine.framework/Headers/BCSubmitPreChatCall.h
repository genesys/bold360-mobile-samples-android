
// NanorepUI version number: v2.3.6.rc2 

//
//  BCSubmitPreChatCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/10/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCSubmitPreChatCallResult.h"
#import "BCCall.h"

@class BCSubmitPreChatCall;

@protocol BCSubmitPreChatCallDelegate <NSObject>
- (void)bcSubmitPreChatCall:(BCSubmitPreChatCall *)submitPreChatCall didFinishWithResult:(BCSubmitPreChatCallResult *)result;
- (void)bcSubmitPreChatCall:(BCSubmitPreChatCall *)submitPreChatCall didFinishWithError:(NSError *)error;
@end

@interface BCSubmitPreChatCall : BCCall

@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, strong)NSDictionary *data;
@property(nonatomic, assign)id<BCSubmitPreChatCallDelegate> delegate;

@end
