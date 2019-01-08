
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "NRChanneling.h"
#import "NRQueryResult.h"


@interface NSDictionary (Parsed)
@property (nonatomic, readonly, copy) NSString *wrapped;
@property (nonatomic, readonly, copy) NSString *asQuery;
@property (nonatomic, readonly, copy) NSString *inJSON;

- (NSArray<NSAttributedString *> *)parseAutoComplete:(UIFont *)font;
@end
