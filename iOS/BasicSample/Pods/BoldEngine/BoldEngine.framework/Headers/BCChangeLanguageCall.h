
// NanorepUI version number: v2.3.6.rc2 

//
//  BCChangeLanguageCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/17/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCChangeLanguageCallResult.h"
#import "BCCall.h"

@class BCChangeLanguageCall;

@protocol BCChangeLanguageCallDelegate <NSObject>
- (void)bcChangeLanguageCall:(BCChangeLanguageCall *)changeLanguageCall didFinishWithResult:(BCChangeLanguageCallResult *)result;
- (void)bcChangeLanguageCall:(BCChangeLanguageCall *)changeLanguageCall didFinishWithError:(NSError *)error;
@end

@interface BCChangeLanguageCall : BCCall

@property(nonatomic, copy)NSString *chatKey;
@property(nonatomic, copy)NSString *language;
@property(nonatomic, assign)id<BCChangeLanguageCallDelegate> delegate;

@end
