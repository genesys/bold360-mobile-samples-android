
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol BCChatSession;

/**
 * @brief The callbacks for the result of \link BCChatSession::changeLanguage:delegate: BCChatSession::changeLanguage:delegate: \endlink.
 * @since Version 1.0
 */
@protocol BCChangeLanguageDelegate <NSObject>

/**
 * @brief The language change call finished successfully.
 * @param chatSession The chat session which the callback is originated from.
 * @param language The new language set.
 * @param branding The new branding dictionary.
 * @since Version 1.0
 */
- (void)bcChatSession:(id<BCChatSession>)chatSession didChangeToLanguage:(NSString *)language withBranding:(NSDictionary *)branding;

/**
 * @brief The language change call finished with error.
 * @param chatSession The chat session which the callback is originated from.
 * @param error The error description about the failure. Check error.code with \link BCErrorCodes.h \endlink to get the exact error.
 * @since Version 1.0
 */
- (void)bcChatSession:(id<BCChatSession>)chatSession didFailToChangeLanguageWithError:(NSError *)error;


@end
