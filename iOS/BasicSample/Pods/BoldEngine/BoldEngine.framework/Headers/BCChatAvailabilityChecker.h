
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCChatAvailability.h"
#import "BCConnectivityManager.h"
#import "BCCancelableImpl.h"

/**
 * @brief The implementation of the chat availability checker. It is for notifying if the chat is available.
 * @since Version 1.0
 */
@interface BCChatAvailabilityChecker : NSObject <BCCancelableImplDelegate>

/**
 * @brief Visitor ID.
 * @since Version 1.0
 */
@property(nonatomic, copy)NSString *visitorId;


/**
 * @brief Constructor.
 * @param connectivityManager The connectivity manager that creates the remote calls for both the chat session and the operator availability checker.
 * @param visitorId The ID of the visitor.
 * @returns A BCChatAvailabilityCheckerImpl instance.
 * @since Version 1.0
 */
- (id)initWithConnectivityManager:(BCConnectivityManager *)connectivityManager visitorId:(NSString *)visitorId;

/**
 * @brief Start checking chat availability.
 * @param cancelable The cancelable which is called to cancel the request.
 * @param delegate The deleagate to call back on.
 * @since Version 1.0
 */
- (void)requestAvailabilityWithCancelable:(BCCancelableImpl *)cancelable delegate:(id<BCChatAvailabilityDelegate>)delegate;


@end
