
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

@interface NRTokenizer : NSObject
- (NSString *)encode:(NSString *)statement;
- (NSString *)decode:(NSString *)statement;
@end
