
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "NRBaseResponse.h"

@interface NRSessionResponse : NRBaseResponse
@property (nonatomic, copy, readonly) NSString *sessionId;
@property (nonatomic, copy) NSNumber *timeout;
@property (nonatomic, copy, readonly) NSNumber *status;
@end
