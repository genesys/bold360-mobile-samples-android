
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol BCChat;
@class BCMessage;

/**
 * @brief Callbacks on message departures and arrivals.
 * @details There ar callbacks for message and aut message arrival to the chat. It also notifies when a message originated from the user was sent to the server.
 * @since Version 1.0
 */
@protocol BCChatMessageDelegate <NSObject>

/**
 * @brief The visitor received a message. It can be an operator, system or a previous visitor message.
 * @param chat The chat which the callback is originated from.
 * @param message The message received.
 * @since Version 1.0
 */
- (void)bcChat:(id<BCChat>)chat didAddMessage:(BCMessage *)message;

/**
 * @brief The visitor received an auto message.
 * @param chat The chat which the callback is originated from.
 * @param message The message received.
 * @since Version 1.0
 */
- (void)bcChat:(id<BCChat>)chat didAddAutoMessage:(BCMessage *)message;


@optional

/**
 * @brief The message send by the visitor reached the server.
 * @param chat The chat which the callback is originated from.
 * @param message The message that was sent.
 * @since Version 1.0
 */
- (void)bcChat:(id<BCChat>)chat didSendVisitorMessage:(BCMessage *)message;


@end
