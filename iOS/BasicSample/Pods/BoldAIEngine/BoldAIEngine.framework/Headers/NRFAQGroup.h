
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "FAQAnswer.h"
#import "NRConversationalResponse.h"

@interface NRFAQGroup : NRBaseResponse
@property (nonatomic, copy, readonly) NSString *title;
@property (nonatomic, copy, readonly) NSNumber *behavior;
@property (nonatomic, copy) NSArray<NRQueryResult *> *data;
@property (nonatomic) NRConversationalResponse *parse;
@end
