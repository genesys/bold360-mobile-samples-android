
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import<Foundation/Foundation.h>
#import "NRResult.h"

@protocol ChannnelPresentDelegate<NSObject>
- (void)presentChannelController:(UIViewController *)controller;
@end

@protocol ChannelPickerDelegate<NSObject>
- (void)didSelectChannel:(NRChanneling *)channel view:(UIView *)channelView forResult:(NRResult *)result;
@property (nonatomic, weak) id<ChannnelPresentDelegate> channnelPresentDelegate;
@end
