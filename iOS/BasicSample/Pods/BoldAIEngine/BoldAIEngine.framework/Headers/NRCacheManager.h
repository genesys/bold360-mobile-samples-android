
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

@interface NRCacheManager : NSObject
+ (void)storeAnswerById:(NSString *)answerId answer:(id)answer;
+ (id)answerById:(NSString *)answerId;
@end
