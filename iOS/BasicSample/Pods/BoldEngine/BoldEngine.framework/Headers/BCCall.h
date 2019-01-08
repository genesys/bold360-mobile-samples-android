
// NanorepUI version number: v2.3.6.rc2 

//
//  BCCall.h
//  VisitorSDK
//
//  Created by vfabian on 03/07/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCRESTCall.h"

/**
 @brief The base class for all remote calls.
 @since Version 1.0
 */
@interface BCCall : NSObject

/**
 @brief The underlying rest call to implement the network communication
 @since Version 1.0
 */
@property(nonatomic, strong)BCRESTCall *restCall;

/**
 @brief The constructor
 @param restCall The underlying rest call.
 @since Version 1.0
 */
- (id)initWithRESTCall:(BCRESTCall *)restCall;

/**
 @brief Start the call.
 @since Version 1.0
 */
- (void)start;

/**
 @brief Cancel the call.
 @since Version 1.0
 */
- (void)cancel;

/**
 @brief Suspend the call.
 @since Version 1.0
 */
- (void)suspend;

/**
 @brief Resume the call.
 @since Version 1.0
 */
- (void)resume;


@end
