
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import <BoldAIEngine/BoldAIEngine.h>
#import "NRResultsView.h"
#import "ChannelPickerDelegate.h"
#import "BaseViewController.h"

@interface NRNoAnswerViewController : BaseViewController
@property (nonatomic, copy) NSString *searchQuery;
@property (nonatomic) NRResult *result;
@property (nonatomic, copy) NSArray<NRFAQGroup *> *groups;
@property (weak, nonatomic) IBOutlet NRResultsView *resultsView;
@property (weak, nonatomic) id<NRResultsViewDelegate> resultsViewDelegate;
@property (weak, nonatomic) id<ChannelPickerDelegate> delegate;
@end
