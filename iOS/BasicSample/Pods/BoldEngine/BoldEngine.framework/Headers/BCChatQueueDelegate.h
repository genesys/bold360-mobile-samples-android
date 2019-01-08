
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * @brief The delegate for callback about the queue position of the visitor.
 * @since Version 1.0
 */
@protocol BCChatQueueDelegate <NSObject>

/**
 * @brief The visitor received an operator busy callback.
 * @param chat The chat which the callback is originated from.
 * @param position The position of the user in the queue.
 * @param unavailableFormAvailable Unavailable form can be requested if the visitor decides to fill it and finish the chat.
 * @since Version 1.0
 */
- (void)bcChat:(id<BCChat>)chat didUpdateQueuePosition:(NSInteger)position unavailableFormAvailable:(BOOL)unavailableFormAvailable;


@end
