
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol BCChatSession;

/**
 * @brief The callbacks for the result of \link BCChatSession::submitUnavailableEmail:delegate: BCChatSession::submitUnavailableEmail:delegate: \endlink.
 * @since Version 1.0
 */
@protocol BCSubmitUnavailableEmailDelegate <NSObject>

/**
 * @brief The submission of email address to send the chat transcript to was successful.
 * @param chatSession The chat session which the callback is originated from.
 * @since Version 1.0
 */
- (void)bcChatSessionDidSubmitUnavailableEmail:(id<BCChatSession>)chatSession;

/**
 * @brief The submission of email address to send the chat transcript to failed.
 * @param chatSession The chat session which the callback is originated from.
 * @param error The error description about the failure. Check error.code with \link BCErrorCodes.h \endlink to get the exact error.
 * @since Version 1.0
 */
- (void)bcChatSession:(id<BCChatSession>)chatSession didFailToSubmitUnavailableEmailWithError:(NSError *)error;

@end
