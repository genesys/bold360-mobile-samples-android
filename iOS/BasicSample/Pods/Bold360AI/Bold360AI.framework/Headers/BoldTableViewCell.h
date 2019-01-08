
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "BoldCellInjector.h"

@protocol BoldTableViewCell

@property (nonatomic, copy, readonly) NSString *cellID;
@property (nonatomic, copy) NSArray *array;
- (void)inject:(id<BoldCellInjector>)injector;

@end
