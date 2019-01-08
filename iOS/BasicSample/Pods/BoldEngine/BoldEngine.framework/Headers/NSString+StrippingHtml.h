
// NanorepUI version number: v2.3.6.rc2 

//
//  NSString+StrippingHtml.h
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
/**
 * @brief Extension for string to strip out all html tahs.
 * @since Version 1.0
 */
@interface NSString (BCStrippingHtml)

/**
 * @brief Strip all html tags.
 * @returns The html tag free string.
 * @since Version 1.0
 */
-(NSString *)bcStringByStrippingHTML;

@end
