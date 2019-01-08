
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

@interface NSURLRequest (Channeling)
@property (nonatomic, readonly) BOOL isNanorep;
@property (nonatomic, readonly) NSInteger type;
@property (nonatomic, copy, readonly) NSString *result;
@property (nonatomic, copy, readonly) NSDictionary *formData;
@end
