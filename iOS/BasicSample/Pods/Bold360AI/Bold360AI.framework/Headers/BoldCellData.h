
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import <BoldEngine/BCFormField.h>
#import <UIKit/UITextInputTraits.h>

@interface BoldCellData : NSObject
@property (nonatomic, strong, readonly) BCFormField *data;
@property (nonatomic, readonly) UIKeyboardType keyboardType;
- (instancetype)initWithData:(BCFormField *)data;
- (instancetype)initWithData:(BCFormField *)data keyboardType:(UIKeyboardType)type;
@end
