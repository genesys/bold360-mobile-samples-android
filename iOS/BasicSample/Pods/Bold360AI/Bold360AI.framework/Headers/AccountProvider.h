
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

@protocol AccountProvider <NSObject>
- (void)provideAccountInfo:(NSString *)apiKey handler:(void (^)(NSDictionary *info))handler;
- (void)storeInfo:(NSDictionary *)info forKey:(NSString *)apiKey;
@end
