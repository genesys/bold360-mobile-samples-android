
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCChat.h"
#import "BCUnavailableReason.h"

@class BCForm;

/** @file */
/**
 * @brief The chat end reasons.
 * @since Version 1.0
 */
typedef enum {
    BCChatEndReasonUndefined, /**< Undefined state for initial state. @since Version 1.0 */
    BCChatEndReasonOperatorFinished, /**< The end of the chat was initiated by the operator. @since Version 1.0 */
    BCChatEndReasonVisitorFinished, /**< The end of the chat was initiated by the visitor. @since Version 1.0 */
    BCChatEndReasonChatTimeout /**< The chat timed out. @since Version 1.0 */
} BCChatEndReason;


/**
 * @brief The delegate that is called back on chat state changes.
 * @since Version 1.0
 */
@protocol BCChatStateDelegate <NSObject>

/**
 * @brief The chat connected.
 * @param chat The chat which the callback is originated from.
 * @since Version 1.0
*/
- (void)bcChatDidConnect:(id<BCChat>)chat;

/**
 * @brief The chat was finished successfully.
 * @param chat The chat which the callback is originated from.
 * @param reason The reason of the finish.
 * @param time The time of the chat finish.
 * @param postChatForm The post chat form.
 * @since Version 1.0
 */
- (void)bcChat:(id<BCChat>)chat didFinishWithReason:(BCChatEndReason)reason time:(NSDate *)time postChatForm:(BCForm *)postChatForm;

/**
 * @brief The chat was accepted by the agent (triggered only once).
 * @param chat The chat which the callback is originated from.
 * @since Version 2.0
 */
- (void)bcChatDidAccept:(id<BCChat>)chat;

/**
 * @brief The chat was finished successfully.
 * @param chat The chat which the callback is originated from.
 * @param reason The reason of the finish.
 * @param unavailableForm The unavailable form.
 * @param message Textural description of the unavailability.
 * @since Version 1.0
 */
- (void)bcChat:(id<BCChat>)chat didFinishWithUnavailableReason:(BCUnavailableReason)reason unavailableForm:(BCForm *)unavailableForm unavailableMessage:(NSString *)message;


/**
 * @brief The chat was finished with an error.
 * @param chat The chat which the callback is originated from.
 * @param error The error description about the failure. Check error.code with \link BCErrorCodes.h \endlink to get the exact error.
 * @since Version 1.0
 */
- (void)bcChat:(id<BCChat>)chat didFinishWithError:(NSError *)error;
@end
