
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
@class NRAlertviewTableViewCell;
@protocol NRAlertviewTableViewCellDelegate <NSObject>

- (void)didSelectCell:(NRAlertviewTableViewCell *)cell;

@end

@interface NRAlertviewTableViewCell : UITableViewCell
@property (nonatomic, copy) NSString *reason;
@property (nonatomic, copy) NSNumber *reasonSelected;
@property (nonatomic, weak) id<NRAlertviewTableViewCellDelegate> delegate;
@end
