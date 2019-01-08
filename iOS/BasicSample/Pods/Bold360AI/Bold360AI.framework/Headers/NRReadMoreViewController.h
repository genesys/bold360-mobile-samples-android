
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "NRResultPresntationView.h"
#import "ChatControllerDelegate.h"
#import "NanorepPersonalInfoHandler.h"
#import "BaseViewController.h"

@class NRReadMoreViewController;
@protocol NRReadMoreViewControllerDelegate<NSObject>
- (void)readmoreController:(NRReadMoreViewController *)readmoreController presentModally:(UIViewController *)controller;
- (void)readmoreController:(NRReadMoreViewController *)readmoreController shouldPresentMessage:(NSString *)message;
@optional
- (void)readmoreController:(NRReadMoreViewController *)readmoreController updateChannels:(NRResult *)result;
@end


@interface NRReadMoreViewController : BaseViewController
@property (nonatomic, copy) NSString *articleId;
@property (nonatomic, weak) id<ChatControllerDelegate> applicationHandler;
@property (nonatomic, copy) NSString *text;
@property (nonatomic, copy) NSString *articleTitle;
@property (nonatomic) NRResult *result;
@property (nonatomic, weak) id<NanorepPersonalInfoHandler> infoHandler;
@property (nonatomic, weak) id<NRReadMoreViewControllerDelegate> delegate;
@property (nonatomic, weak) IBOutlet NRResultPresntationView *resultView;
@property (nonatomic, strong) id<ChannelPickerDelegate> channelPickerDelegate;

- (void)dismiss;
@end
