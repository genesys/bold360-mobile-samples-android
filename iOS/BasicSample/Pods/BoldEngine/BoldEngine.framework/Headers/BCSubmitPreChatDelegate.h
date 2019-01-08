
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "BCUnavailableReason.h"

@protocol BCChatSession;
@protocol BCChat;
@class BCForm;

/**
 * @brief The callbacks for the result of \link BCChatSession::submitPreChat:andStartChatWithDelegate: BCChatSession::submitPreChat:andStartChatWithDelegate: \endlink.
 * @since Version 1.0
 */
@protocol BCSubmitPreChatDelegate <NSObject>

/**
 * @brief The submission finished successfully, the chat is started.
 * @param chatSession The chat session which the callback is originated from.
 * @param chat The chat instance that is created for the chat.
 * @since Version 1.0
 */
- (void)bcChatSessionDidSubmitPreChat:(id<BCChatSession>)chatSession andDidStartChat:(id<BCChat>)chat;

/**
 * @brief The chat is currently unavailable
 * @param chatSession The created chat session.
 * @param reason The reason for chat to be unavailable.
 * @param unavailableForm The unavailable chat form description.
 * @param message The textural description of unavailable reason.
 * @since Version 1.0
 */
- (void)bcChatSession:(id<BCChatSession>)chatSession didSubmitPreChatToUnavailableChatWithReason:(BCUnavailableReason)reason unavailableForm:(BCForm *)unavailableForm unavailableMessage:(NSString *)message;

/**
 * @brief The submission of the pre-chat or the starting of the chat failed.
 * @param chatSession The chat session which the callback is originated from.
 * @param error The error description about the failure. Check error.code with \link BCErrorCodes.h \endlink to get the exact error.
 * @since Version 1.0
 */
- (void)bcChatSession:(id<BCChatSession>)chatSession didFailToSubmitPreChat:(NSError *)error;

@end
