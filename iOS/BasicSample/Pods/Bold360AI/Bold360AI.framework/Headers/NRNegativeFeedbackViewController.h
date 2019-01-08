
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "NRResultPresntationView.h"
#import "NRAbstractViews.h"

@class NRNegativeFeedbackViewController;
@protocol NRNegativeFeedbackViewControllerDelegate <NSObject>

- (void)didSubmitNegativeFeedback:(NRNegativeFeedbackViewController *)controller;
- (void)didCancelFeedback:(NRNegativeFeedbackViewController *)controller;
- (void)shouldUpdateChannels:(NRNegativeFeedbackViewController *)controller;

@end

@interface NRNegativeFeedbackViewController : UIViewController
@property (nonatomic, copy) NSString *titleText;
@property (nonatomic, weak) id<NRNegativeFeedbackViewControllerDelegate> deleagte;
@property (nonatomic, weak) NRResultPresntationView *presentationView;
@property (nonatomic, copy, readonly) NSString *text;
@property (nonatomic, copy, readonly) NSString *pickedOption;
@property (nonatomic, strong) UIView <NRNegativeFeedback> *popupView;
@end
