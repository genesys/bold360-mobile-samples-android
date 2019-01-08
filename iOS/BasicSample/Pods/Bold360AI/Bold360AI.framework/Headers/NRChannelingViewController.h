
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "ExtraData.h"

@protocol NRChannelingViewControllerDelegate <NSObject>

- (void)channelController:(UIViewController *)controller didCancel:(NSString *)channelLink;
- (void)channelController:(UIViewController *)controller didFailWithError:(NSError *)error;
- (void)channelController:(UIViewController *)controller didSend:(NSString *)channelLink;
- (void)didFetchFormData:(ExtraData *)formData;

@end

@interface NRChannelingViewController : UIViewController 
@property (nonatomic, copy) NSURL *channelURL;
@property (nonatomic, weak) id<NRChannelingViewControllerDelegate> delegate;
@property (nonatomic, copy) NSString *channelTitle;
@end
