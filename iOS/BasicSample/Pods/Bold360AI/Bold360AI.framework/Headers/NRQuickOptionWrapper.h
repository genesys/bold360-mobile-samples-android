
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "NRQuickOption.h"


@interface NRQuickOptionWrapper : NSObject
- (void)setKind:(NRQuickOptionKind)kind;
- (void)setType:(NRQuickOptionType)type;

@property(nonatomic, copy) NSString *text;
@property(nonatomic, readonly, copy) NSString *inJSON;
@property (nonatomic) BOOL cached;

- (void)addQuickOption:(NRQuickOption *)quickOption;
@end
