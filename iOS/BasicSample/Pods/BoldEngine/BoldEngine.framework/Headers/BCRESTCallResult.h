
// NanorepUI version number: v2.3.6.rc2 

//
//  BCRESTCallResult.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/11/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 Base class for the REST call results.
 @since Version 1.0
 */
@interface BCRESTCallResult : NSObject

/**
 YES if the status is success.
 @since Version 1.0
 */
@property(nonatomic, assign)BOOL statusSuccess;

/**
 Result status in string.
 @since Version 1.0
 */
@property(nonatomic, strong)NSString *status;

/**
 Error string if the status was error.
 @since Version 1.0
 */
@property(nonatomic, strong)NSString *errorMessage;

@end
