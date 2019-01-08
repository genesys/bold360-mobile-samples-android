
// NanorepUI version number: v2.3.6.rc2 

//
//  BCRESTCallResultParser.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/28/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCRESTCallResult.h"

/**
 A class for REST calls. It handles the underlying connection, parsing, running the paring on a background task.
 @since Version 1.0
 */
@interface BCRESTCallResultParser : NSObject

/**
 A class for REST calls. It handles the underlying connection, parsing, running the paring on a background task.
 @since Version 1.0
 @param data The data to be parsed.
 @param error Output error object if the data cannot be parsed.
 @returns The object parsed.
 @since Version 1.0
 */
- (NSObject *)parse:(NSData *)data error:(__autoreleasing NSError**)error;

/**
 Fills the result success values for the result.
 @since Version 1.0
 @param dictionary PArsed dictionary of parameters.
 @param result The result to be filled.
 @since Version 1.0
 */
- (void)fillSuccessAndErrorFromDictionary:(NSDictionary *)dictionary forResult:(BCRESTCallResult *)result;

/**
 Returns the string representation of the value.
 @since Version 1.0
 @param object The object to get the string representation.
 @returns The string representation.
 @since Version 1.0
 */
- (NSString *)stringValueOfObject:(NSObject *)object;
@end
