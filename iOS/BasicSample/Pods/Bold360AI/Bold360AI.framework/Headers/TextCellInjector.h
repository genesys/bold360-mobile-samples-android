
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "BoldCellInjector.h"
#import <BoldEngine/NSString+BCValidation.h>

@interface TextCellInjector : NSObject <BoldCellInjector>
@property (nonatomic, readonly) UIKeyboardType keyboardType;
@property (nonatomic) BOOL isValid;
@end
