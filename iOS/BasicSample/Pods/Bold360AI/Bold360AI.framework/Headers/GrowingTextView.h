
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>

/**
 * An auto-layout based light-weight UITextView subclass which automatically grows and shrinks
 based on the size of user input and can be constrained by maximal and minimal height - all without
 a single line of code.
 
 Made primarely for use in Interface builder and only works with Auto layout.
 
 Usage: subclass desired UITextView in IB and assign min-height and max-height constraints
 */

/************************************************************/
// MARK: - GrowingTextViewDelegate
/************************************************************/

@protocol GrowingTextViewDelegate
- (void)heightConstraintShouldChange;
- (void)textViewDidBeginEditing;
- (void)textViewShouldReplaceText:(NSString *)text;
@end

/************************************************************/
// MARK: - GrowingTextView
/************************************************************/

@interface GrowingTextView : UITextView <UITextViewDelegate>
@property (nonatomic, weak) id<GrowingTextViewDelegate> growingTextDelegate;
@end
