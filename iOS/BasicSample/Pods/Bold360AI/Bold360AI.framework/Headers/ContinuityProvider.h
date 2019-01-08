
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

/**
 Provider protocol used for:
 - storing id's and values that enable chat continuity.
 - fetching id's and values to activate chat continuity.
 
 - Important: In order to continue chat from last point
 you must impliment this provider methods.
 
 */
@protocol ContinuityProvider <NSObject>

/**
 Update key value that will be used for chat continuity.

 @param params used for continue chat from last point by id.
 */
- (void)updateContinuityInfo:(NSDictionary<NSString *, NSNumber *> *)params;

/**
 Fetch stored key value for chat continuity.

 Block Returns: NSNumber containing
 relevant value to continue from last chat point.
 */
- (void)fetchContinuityForKey:(NSString *)key handler:(void(^)(NSNumber * value))handler;
@end
