
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "BoldCellData.h"

/************************************************************/
// MARK: - BoldCellInjector
/************************************************************/

@protocol BoldCellInjector
@property (nonatomic, copy, readonly) NSString *cellType;
@property (nonatomic, strong, readonly) BoldCellData *cellData;
@property (nonatomic, readonly) BOOL isValid;
- (instancetype)initWithData:(BCFormField *)cellData;
- (BOOL)validateContent:(NSString *)text;
@end
