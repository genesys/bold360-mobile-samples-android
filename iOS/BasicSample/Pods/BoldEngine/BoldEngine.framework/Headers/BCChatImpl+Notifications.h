
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCChatImpl.h"

/**
 * @brief The notification propagating category for the BCChatImpl.
 * @since Version 1.0
 */
@interface BCChatImpl (Notifications)

/**
 * @brief An operator started or stopped typing.
 * @param person The operator.
 * @param typing If the typing started or stopped.
 * @since Version 1.0
 */
- (void)propagateDidUpdateTyper:(BCPerson *)person typing:(BOOL)typing;

/**
 * @brief The message send by the visitor reached the server.
 * @param typing The typing state to be propagated.
 * @since Version 1.0
 */
- (void)propagateDidSendVisitorTyping:(BOOL)typing;

/**
 * @brief The visitor received a message. It can be an operator, system or a visitor messagethat was sent before.
 * @param message The message received.
 * @since Version 1.0
 */
- (void)propagateDidAddMessage:(BCMessage *)message;

/**
 * @brief The visitor received a message from the operator.
 * @param message The message received.
 * @since Version 1.0
 */
- (void)propagateDidAddAutoMessage:(BCMessage *)message;

/**
 * @brief The message send by the visitor reached the server.
 * @param message The message that was sent.
 * @since Version 1.0
 */
- (void)propagateDidSendVisitorMessage:(BCMessage *)message;

/**
 * @brief The visitor received an operator busy callback.
 * @param position The position of the user in the queue.
 * @param unavailableFormAvailable Unavailable form can be requested if the visitor decides to fill it and exit the chat.
 * @since Version 1.0
 */
- (void)propagateDidUpdateQueuePosition:(NSInteger)position unavailableFormAvailable:(BOOL)unavailableFormAvailable;

/**
 * @brief The chat is started.
 * @since Version 1.0
 */
- (void)propagateDidConnect;

/**
 * @brief The chat is accepted.
 * @since Version 1.0
 */
- (void)propagateDidAccept;

/**
 * @brief The chat was finished successfully.
 * @param reason The reason of the finish.
 * @param time The time of the chat finish.
 * @param postChatForm Post chat form description.
 * @since Version 1.0
 */
- (void)propagateDidFinishWithReason:(BCChatEndReason)reason time:(NSDate *)time postChatForm:(BCForm *)postChatForm;

/**
 * @brief The chat was canceled by the user and the unavailable info is presented.
 * @param reason The reason of the finish.
 * @param unavailableForm The unavailableForm.
 * @param message Textural description of the unavailability.
 * @since Version 1.0
 */
- (void)propagateDidFinishWithUnavailableReason:(BCUnavailableReason)reason unavailableForm:(BCForm *)unavailableForm unavailableMessage:(NSString *)message;

/**
 * @brief The chat was finished with an error.
 * @param error The error which caused the chat to finish.
 * @since Version 1.0
 */
- (void)propagateDidFinishWithError:(NSError *)error;

@end
