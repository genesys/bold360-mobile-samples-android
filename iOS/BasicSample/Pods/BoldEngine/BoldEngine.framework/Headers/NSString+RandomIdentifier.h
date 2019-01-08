
// NanorepUI version number: v2.3.6.rc2 

//
//  NSString+RandomIdentifier.h
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * NSString category to implement a random identifier string generation.
 * @since Version 1.0
 */
@interface NSString (BCRandomIdentifier)

/**
 * Generates a string that contains a random number.
 * @since Version 1.0
 */
+ (NSString *)bcRandomIdentifier;

@end
