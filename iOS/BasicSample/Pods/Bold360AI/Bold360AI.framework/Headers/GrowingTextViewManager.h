
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "GrowingTextView.h"

/************************************************************/
// MARK: - GrowingTextViewManager
/************************************************************/

@interface GrowingTextViewManager : NSObject

- (instancetype)initWithLayoutConstraints:(NSArray *)layoutConstraints
                              textView:(GrowingTextView *)textView;
- (void)changeHeightConstraintWithTop:(NSLayoutConstraint *)topHeightConstraint
        bottomHeightConstraint:(NSLayoutConstraint *)bottomHeightConstraint;
@end
