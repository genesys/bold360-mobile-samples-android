
// NanorepUI version number: v2.3.6.rc2 

//
//  BCBuiltInLocalisation.h
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * @brief Built in localizations for cases when the localization dictionarry is not available.
 * @since Version 1.0
 */
@interface BCBuiltInLocalisation : NSObject

/**
 * @brief Built in localizations for chat title.
 * @returns Chat title.
 * @since Version 1.0
 */
+ (NSString *)localisedStringForChatTitleWithLanguage:(NSString *)language;

/**
 * @brief Built in localizations for network error.
 * @returns Network error text.
 * @since Version 1.0
 */
+ (NSString *)localisedStringForNetworkErrorWithLanguage:(NSString *)language;

@end
