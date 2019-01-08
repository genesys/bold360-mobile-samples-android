
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "NRTableViewCell.h"

@class NRTitleTableViewCell;
@protocol NRTitleTableViewCellDelegate  <NSObject>

- (void)updatedDynamicHeight:(NRTitleTableViewCell *)cell;
- (void)didClickArrow:(NRTitleTableViewCell *)cell;

@end

@interface NRTitleTableViewCell : UITableViewCell
@property (nonatomic, strong) NRResult *result;
@property (weak, nonatomic) IBOutlet UIView *resultView;
@property (nonatomic) NRAnimationType animationType;
@property (nonatomic, weak) id<NRTitleTableViewCellDelegate> delegate;
@end
