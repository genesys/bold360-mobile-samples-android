
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "NRResult.h"

@class NRTableViewCell;
@protocol NRTableViewCellDelegate <NSObject>

- (void)didSelectResult:(NRTableViewCell *)cell;
- (void)didSelectLinkedArticle:(NSString *)articleId;
- (void)cell:(NRTableViewCell *)cell didLike:(BOOL)isPositive;
- (void)cell:(NRTableViewCell *)cell didSelectChannel:(NRChanneling *)channel;

@end

@interface NRTableViewCell : UITableViewCell
@property (nonatomic, strong) NRResult *result;
@property (nonatomic, weak) id<NRTableViewCellDelegate> delegate;
@end
