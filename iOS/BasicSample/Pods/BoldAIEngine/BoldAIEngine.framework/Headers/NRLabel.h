
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import "NRFAQGroup.h"

@interface NRLabel : NRFAQGroup <NSCopying>
@property (nonatomic, copy) NSString *labelId;
@property (nonatomic, copy, readonly) NSString *context;
@property (nonatomic, copy, readonly) NSString *labelName;
@property (nonatomic, copy, readonly) NSURL *iconUrl;
@property (nonatomic) NSData *iconData;
@end
