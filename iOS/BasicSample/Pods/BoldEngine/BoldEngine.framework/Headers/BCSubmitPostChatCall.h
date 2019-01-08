
// NanorepUI version number: v2.3.6.rc2 

//
//  BCSubmitPostChatCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/10/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCSubmitPostChatCallResult.h"
#import "BCCall.h"

@class BCSubmitPostChatCall;

@protocol BCSubmitPostChatCallDelegate <NSObject>
- (void)bcSubmitPostChatCall:(BCSubmitPostChatCall *)submitPostChatCall didFinishWithResult:(BCSubmitPostChatCallResult *)result;
- (void)bcSubmitPostChatCall:(BCSubmitPostChatCall *)submitPostChatCall didFinishWithError:(NSError *)error;
@end

@interface BCSubmitPostChatCall : BCCall

@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, strong)NSDictionary *data;
@property(nonatomic, assign)id<BCSubmitPostChatCallDelegate> delegate;

@end
