
// NanorepUI version number: v2.3.6.rc2 

//
//  BCSubmitUnavailableEmailCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/10/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCSubmitUnavailableEmailCallResult.h"
#import "BCCall.h"

@class BCSubmitUnavailableEmailCall;

@protocol BCSubmitUnavailableEmailCallDelegate <NSObject>
- (void)bcSubmitUnavailableEmailCall:(BCSubmitUnavailableEmailCall *)submitUnavailableEmailCall didFinishWithResult:(BCSubmitUnavailableEmailCallResult *)result;
- (void)bcSubmitUnavailableEmailCall:(BCSubmitUnavailableEmailCall *)submitUnavailableEmailCall didFinishWithError:(NSError *)error;
@end

@interface BCSubmitUnavailableEmailCall : BCCall

@property(nonatomic, strong)NSString *from;
@property(nonatomic, strong)NSString *subject;
@property(nonatomic, strong)NSString *body;
@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, assign)id<BCSubmitUnavailableEmailCallDelegate> delegate;

@end
