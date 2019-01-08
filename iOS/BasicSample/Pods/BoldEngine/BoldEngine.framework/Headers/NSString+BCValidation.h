
// NanorepUI version number: v2.3.6.rc2 

//
//  NSString+BCEmailAddressValidation.h
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * @brief String extension for validating if the string is a valid email address or phone number.
 * @since Version 1.0
 */
@interface NSString (BCValidation)

/**
 * @brief Tests if the string is a valid email address.
 * @returns YES if the string is a valid email address.
 * @since Version 1.0
 */
- (BOOL)bcIsValidEmailAddress;

/**
 * @brief Tests if the string is a valid phone number.
 * @returns YES if the string is a valid phone number.
 * @since Version 1.0
 */
- (BOOL)bcIsValidPhoneNumber;

- (BOOL)bcIsValidAlphabets;

@end
