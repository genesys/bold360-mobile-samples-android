
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol BCChat;
@class BCPerson;

/**
 * @brief Callbacks on typing events.
 * @details It has a callback on the operator starting or stopping typing. It has also a callback on the visitor typing message sending.
 * @since Version 1.0
 */
@protocol BCChatTyperDelegate <NSObject>

/**
 * @brief An operator started or stopped typing.
 * @param chat The chat which the callback is originated from.
 * @param person The operator.
 * @param typing If the operator started or stopped typing.
 * @since Version 1.0
 */
- (void)bcChat:(id<BCChat>)chat didUpdateTyper:(BCPerson *)person typing:(BOOL)typing;

@optional
/**
 * @brief The message send by the visitor reached the server.
 * @param chat The chat which the callback is originated from.
 * @param typing Yes if any of the operators is typing.
 * @since Version 1.0
 */
- (void)bcChat:(id<BCChat>)chat didSendVisitorTyping:(BOOL)typing;


@end
