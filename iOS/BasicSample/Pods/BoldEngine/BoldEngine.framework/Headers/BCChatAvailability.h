
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCUnavailableReason.h"

@protocol BCCancelable;

/**
 * @brief Delegate callback that is used to return the result of \link BCAccount::getChatAvailabilityWithDelegate: \endlink.
 * @since Version 1.0
 */
@protocol BCChatAvailabilityDelegate <NSObject>

/**
 * @brief The chat is available.
 * @param cancelable The cancelable object of the request calling this check.
 * @since Version 1.0
 */
- (void)bcChatAvailabilityChatAvailable:(id<BCCancelable>)cancelable;

/**
 * @brief The chat is unavailable.
 * @param cancelable The cancelable object of the request calling this check.
 * @param reason The reason why the chat is not available.
 * @since Version 1.0
 */
- (void)bcChatAvailability:(id<BCCancelable>)cancelable chatUnavailableForReason:(BCUnavailableReason)reason;

/**
 * @brief Chat availability could not be checked.
 * @param cancelable The cancelable object of the request calling this check.
 * @param error The error object for the current failure. Check error.code with \link BCErrorCodes.h \endlink to get the exact error.
 * @since Version 1.0
 */
- (void)bcChatAvailability:(id<BCCancelable>)cancelable didFailWithError:(NSError *)error;

@end
