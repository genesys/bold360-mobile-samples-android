
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "NRTitleTableViewCell.h"
#import "BaseViewController.h"

@class NRResultsView;
@protocol NRResultsViewDelegate <NSObject>

- (void)didSelectResult:(NRResultsView *)cell;

@end

@interface NRResultsView : UIView
@property (nonatomic, copy) NSArray<NRResult *> *results;
@property (nonatomic, weak) id<NRResultsViewDelegate> delegate;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (nonatomic, readonly) CGRect selectedRect;
@property (nonatomic, readonly) NRResult *selectedResult;
@property (nonatomic, copy) NSString *searchQuery;
@property (nonatomic) NRAnimationType animationType;
@property (nonatomic, copy) NSString *faqTitle;
- (void)allowSelection;
@property (nonatomic, strong, readonly) BaseViewController *attachController;

@end
