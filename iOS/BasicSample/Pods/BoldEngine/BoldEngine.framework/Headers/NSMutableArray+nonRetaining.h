
// NanorepUI version number: v2.3.6.rc2 

//
//  NSMutableArray+nonRetaining.h
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * @brief Category for arrays that does not retain their elements.
 * @since Version 1.0
 */
@interface NSMutableArray (BCNonRetaining)

/**
 * @brief Return an array that does not retain any element.
 * @param itemNum Number of initial elements.
 * @returns The array object.
 * @since Version 1.0
 */
+ (id)bcNonRetainingArrayWithCapacity:(NSUInteger)itemNum;

@end
