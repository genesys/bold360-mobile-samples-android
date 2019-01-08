
// NanorepUI version number: v2.3.6.rc2 

//
//  BCGetUnavailableFormCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/10/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCGetUnavailableFormCallResult.h"
#import "BCCall.h"

@class BCGetUnavailableFormCall;

@protocol BCGetUnavailableFormCallDelegate <NSObject>
- (void)bcGetUnavailableFormCall:(BCGetUnavailableFormCall *)getUnavailableFormCall didFinishWithResult:(BCGetUnavailableFormCallResult *)result;
- (void)bcGetUnavailableFormCall:(BCGetUnavailableFormCall *)getUnavailableFormCall didFinishWithError:(NSError *)error;
@end

@interface BCGetUnavailableFormCall : BCCall

@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, strong)NSString *clientId;
@property(nonatomic, assign)id<BCGetUnavailableFormCallDelegate> delegate;

@end
