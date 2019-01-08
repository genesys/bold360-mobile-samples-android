
// NanorepUI version number: v2.3.6.rc2 

//
//  NSObject+nilOrValue.h
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * @brief Extension for object to convert to a string or number, or if it cannot be done , return a nil or 0.
 * @since Version 1.0
 */
@interface NSObject (BCNilOrValue)

/**
 * @brief String or nil value of the object.
 * @returns The string value.
 * @since Version 1.0
 */
- (NSString *)bcNilOrStringValue;

/**
 * @brief Integer or nil value of the object.
 * @returns The integer value.
 * @since Version 1.0
 */
- (NSInteger)bcNilOrIntegerValue;

/**
 * @brief Bool or nil value of the object.
 * @returns The bool value.
 * @since Version 1.0
 */
- (BOOL)bcNilOrBoolValue;

@end
