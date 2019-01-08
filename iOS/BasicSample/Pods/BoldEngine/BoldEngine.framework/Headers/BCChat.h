
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCChatTyperDelegate.h"
#import "BCChatMessageDelegate.h"
#import "BCChatQueueDelegate.h"
#import "BCChatStateDelegate.h"
#import "BCErrorCodes.h"

@protocol BCChat;
@class BCMessage;
@class BCPerson;

/**
 * @brief A protocol of an object that sends and receives chat messages, states and events.
 * @details <p>A chat is a protocol that lets the user to do the actual chatting. It can send messages and can receive messages from the operator. Chats are created on chat session creations when there is no pre chat defined, or on submission of pre chat.</p> <p>The current chat is returned by \link BCCreateChatSessionDelegate::bcAccount:didCreateChatWithoutPreChat:andDidStartChat:\endlink and \link BCSubmitPreChatDelegate::bcChatSessionDidSubmitPreChat:andDidStartChat: bcChatSessionDidSubmitPreChat:andDidStartChat: \endlink callbacks. If the chat exists it is also reachable on \link BCChatSession::chat \endlink property.</p>
 * @since Version 1.0
 */
@protocol BCChat <NSObject>

/**
 * @brief The list of messages that were sent by the operator and the visitor in timed order. It is an array of \link BCMessage \endlink objects.
 * @since Version 1.0
 */
@property (nonatomic, readonly)NSArray *messages;

/**
 * @brief The auto or system message for the current state. It can occur if the user started a chat but it was not still answered by any operator.
 * @since Version 1.0
 */
@property (nonatomic, readonly)BCMessage *currentSystemMessage;

/**
 * @brief The id of the last chat message received from the server.
 * @since Version 1.0
 */
@property (nonatomic, readonly)long long lastChatMessageId;

/**
 * @brief The current visitor data.
 * @since Version 1.0
 */
@property (nonatomic, readonly)BCPerson *visitor;

/**
 * @brief Add a chat type delegate. Multiple delegates are enabled.
 * @since Version 1.0
 */
- (void)addChatTyperDelegate:(id<BCChatTyperDelegate>)delegate;

/**
 * @brief Remove a chat type delegate.
 * @since Version 1.0
 */
- (void)removeChatTyperDelegate:(id<BCChatTyperDelegate>)delegate;

/**
 * @brief Add a chat message delegate. Multiple delegates are enabled.
 * @since Version 1.0
 */
- (void)addChatMessageDelegate:(id<BCChatMessageDelegate>)delegate;

/**
 * @brief Remove a chat message delegate.
 * @since Version 1.0
 */
- (void)removeChatMessageDelegate:(id<BCChatMessageDelegate>)delegate;

/**
 * @brief Add a chat queue delegate. Multiple delegates are enabled.
 * @since Version 1.0
 */
- (void)addChatQueueDelegate:(id<BCChatQueueDelegate>)delegate;

/**
 * @brief Remove a chat queue delegate.
 * @since Version 1.0
 */
- (void)removeChatQueueDelegate:(id<BCChatQueueDelegate>)delegate;

/**
 * @brief Add a chat state delegate. Multiple delegates are enabled.
 * @since Version 1.0
 */
- (void)addChatStateDelegate:(id<BCChatStateDelegate>)delegate;

/**
 * @brief Remove a state type delegate.
 * @since Version 1.0
 */
- (void)removeChatStateDelegate:(id<BCChatStateDelegate>)delegate;

/**
 * @brief Finish the chat and answer post chat if available.
 * @since Version 1.0
 */
- (void)finishChat;

/**
 * @brief Finish the chat and gather unavailable chat form to answer it.
 * @since Version 1.0
 */
- (BOOL)finishChatToAnswerUnavailableForm;

/**
 * @brief Send a message to the operator.
 * @param message The message to be sent.
 * @since Version 1.0
 */
- (void)sendMessage:(BCMessage *)message;

/**
 * @brief Send a message text, to the operator. It is sent with the current visitor settings.
 * @param messageText The string value of the message to be sent.
 * @returns The message object that is actually sent.
 * @since Version 1.0
 */
- (BCMessage *)sendMessageText:(NSString *)messageText;

/**
 * @brief Send if the visitor is typing.
 * @param visitorTyping The visitor's current typing state.
 * @since Version 1.0
 */
- (void)sendVisitorTyping:(BOOL)visitorTyping;

@end
