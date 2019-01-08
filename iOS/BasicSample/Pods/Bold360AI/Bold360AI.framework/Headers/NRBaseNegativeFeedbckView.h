
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "NRAbstractViews.h"
#import "NSBundle+UIBundle.h"

@interface NRBaseNegativeFeedbckView : UIView <NRNegativeFeedback>
@property (nonatomic, weak) IBOutlet UIView *textHolder;
@property (weak, nonatomic) IBOutlet UILabel *title;
@property (weak, nonatomic) IBOutlet UIButton *closeButton;
@property (weak, nonatomic) IBOutlet UIButton *submitButton;

- (IBAction)close:(UIButton *)sender;
- (IBAction)submit:(UIButton *)sender;

- (void)addTextView:(UIView *)view;
@end
