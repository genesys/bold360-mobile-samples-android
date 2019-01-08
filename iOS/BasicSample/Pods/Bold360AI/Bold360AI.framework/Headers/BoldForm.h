
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "BrandedForm.h"
#import "BoldFormSelection.h"

/************************************************************/
// MARK: - BoldFormDelegate Protocol
/************************************************************/

@protocol BoldFormDelegate
- (void)submitForm:(BrandedForm *)form;
@end

/************************************************************/
// MARK: - BoldForm Protocol
/************************************************************/

@protocol BoldForm

/**
 The form data.
 */
@property (nonatomic, strong) BrandedForm *form;
@property (nonatomic, strong) id<BoldFormDelegate> delegate;

@optional

/**
 The selection object, if not set will be `DefaultBoldFormSelection`
 */
@property (nonatomic, strong) id<BoldFormSelection> boldFormSelection;

/**
 The handler for form fields selection (pre/post chat)
 */
@property (nonatomic, copy) void (^formHandler)(NSDictionary *);

@end
