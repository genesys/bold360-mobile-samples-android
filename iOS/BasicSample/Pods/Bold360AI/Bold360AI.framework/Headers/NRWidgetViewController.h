
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "NRDataSource.h"
#import "NRAbstractViews.h"
#import "NanorepPersonalInfoHandler.h"

@class NRWidgetViewController;
@protocol NRWidgetViewControllerCustomData <NSObject>

@optional
- (BOOL)shouldOpenURL:(NSURL *)url articleId:(NSString *)articleId;
- (NSString *)widgetController:(NRWidgetViewController *)controller htmlCustomDataForKey:(NSString *)key;
- (NSDictionary *)widgetController:(NRWidgetViewController *)controller valuesForKeys:(NSArray *)keys;
@end

@protocol NRWidgetViewControllerDelegate <NSObject>

@optional
- (void)widget:(NRWidgetViewController *)widget didFetchConfiguration:(NRConfiguration *)configuration;
- (void)didAddNewPage:(NRWidgetViewController *)controller;
- (void)iconAtUrl:(NSURL *)url handler:(void(^)(NSData *iconData))handler;
@end




@interface NRWidgetViewController : UIViewController
@property (nonatomic) NRAnimationType animationType;
@property (nonatomic, copy) NSString *customEmailChannelAddress;
@property (nonatomic, weak) id<NRWidgetViewControllerCustomData> customData;
@property (nonatomic, weak) id<NanorepViewAdapter> viewAdapter;
@property (nonatomic, weak) id<NRWidgetViewControllerDelegate> delegate;
@property (nonatomic) NRConfiguration *configuration;
@property (nonatomic, weak) id<NanorepPersonalInfoHandler> infoHandler;

@property (nonatomic, weak) UIViewController *mainViewController;

@property (nonatomic) BOOL disableNavigation;
- (void)performBackWithCompletion:(void(^)())completion;
@property (nonatomic, readonly) NSInteger pages;
@property (nonatomic) BOOL hideCloseButton;
@end
