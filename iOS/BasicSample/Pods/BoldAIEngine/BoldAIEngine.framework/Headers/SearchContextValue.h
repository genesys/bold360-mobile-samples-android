
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "NRBaseResponse.h"

@interface SearchContextValue : NRBaseResponse
@property (nonatomic, copy, readonly) NSString *name;
@property (nonatomic, readonly) NSInteger flags;
@property (nonatomic, copy, readonly) NSNumber *id;
@property (nonatomic, copy, readonly) NSString *selectionText;
@end
