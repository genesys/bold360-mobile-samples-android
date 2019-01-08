
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCUnavailableReason.h"

@class BCAccount;
@class BCForm;
@protocol BCChatSession;
@protocol BCChat;

/**
 * @brief The delegate callback from \link BCAccount::accountWithAccessKey: BCAccount::accountWithAccessKey: \endlink that receives events about a chat being created.
 * @since Version 1.0
 */
@protocol BCCreateChatSessionDelegate <NSObject>

/**
 * @brief The chat session was created without having pre chat and the chat was started.
 * @param account The account that created the chat.
 * @param chatSession The created chat session.
 * @param chat The created and started chat. The delegates need to be added here to tha chat.
 * @since Version 1.0
 */
- (void)bcAccount:(BCAccount *)account didCreateChatWithoutPreChat:(id<BCChatSession>)chatSession andDidStartChat:(id<BCChat>)chat;

/**
 * @brief The chat session was created, pre chat needs to be filled.
 * @param account The account that created the chat.
 * @param chatSession The created chat session.
 * @param preChat The pre chat form.
 * @since Version 1.0
 */
- (void)bcAccount:(BCAccount *)account didCreateChat:(id<BCChatSession>)chatSession withPreChat:(BCForm *)preChat;

/**
 * @brief The chat is currently unavailable.
 * @param account The account that created the chat.
 * @param chatSession The created chat session.
 * @param reason  The reason for chat to be unavailable.
 * @param unavailableForm The unavailable chat form.
 * @param message The textural description of unavailable reason.
 * @since Version 1.0
 */
- (void)bcAccount:(BCAccount *)account didCreateChat:(id<BCChatSession>)chatSession unavailableWithReason:(BCUnavailableReason)reason unavailableForm:(BCForm *)unavailableForm unavailableMessage:(NSString *)message;

/**
 * @brief The chat failed to be created.
 * @param account The account that created the chat.
 * @param error The error description about the failure. Check error.code with \link BCErrorCodes.h \endlink to get the exact error.
 * @since Version 1.0
 */
- (void)bcAccount:(BCAccount *)account didFailToCreateWithError:(NSError *)error;

@end
