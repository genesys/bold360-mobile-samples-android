
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol BCChatSession;

/**
 * @brief The delegate that will be notified if an operator has attempted to re-capture an attempted chat session while chat was unavailable.
 * @since Version 1.0
 */
@protocol BCChatRecaptureDelegate <NSObject>

/**
 * @brief While the user fills the unavailable operator email for, an operator can become active. This method is called on this event.
 * @param chatSession The chat session which the callback is originated from.
 * @since Version 1.0
 */
- (void)bcChatSessionRecaptureAvailable:(id<BCChatSession>)chatSession;

@end
