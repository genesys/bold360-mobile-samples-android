
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol BCChatSession;

/**
 * @brief The callbacks for the result of \link BCChatSession::submitPostChat:delegate: BCChatSession::submitPostChat:delegate: \endlink.
 * @since Version 1.0
 */
@protocol BCSubmitPostChatDelegate <NSObject>

/**
 * @brief The post chat submission call finished successfully.
 * @param chatSession The chat session which the callback is originated from.
 * @since Version 1.0
 */
- (void)bcChatSessionDidSubmitPostChat:(id<BCChatSession>)chatSession;

/**
 * @brief The post chat submission call finished with error.
 * @param chatSession The chat session which the callback is originated from.
 * @param error The error description about the failure. Check error.code with \link BCErrorCodes.h \endlink to get the exact error.
 * @since Version 1.0
 */
- (void)bcChatSession:(id<BCChatSession>)chatSession didFailToSubmitPostChatWithError:(NSError *)error;


@end

