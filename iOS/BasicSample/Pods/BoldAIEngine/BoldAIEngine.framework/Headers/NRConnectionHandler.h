
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

@protocol NRConnectionHandler<NSObject>
- (void)open:(nonnull NSURLRequest *)request completion:(nonnull void (^)(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error))completion;
- (nullable NSData *)fetchDataAtRequest:(nonnull NSURLRequest *)request error:(NSError *_Nullable*_Nullable)error;
@end
