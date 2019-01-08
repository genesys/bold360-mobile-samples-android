
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import <BoldAIEngine/BoldAIEngine.h>

@class NRContextPickerViewController;
@protocol NRContextPickerViewControllerDelegate <NSObject>

- (void)controller:(NRContextPickerViewController *)controller didPickContext:(NSString *)context response:(NRSearchResponse *)searchResponse;

@end

@interface NRContextPickerViewController : UIViewController
@property (nonatomic) SearchAnswer *answer;
@property (nonatomic, copy) NSArray *contextNames;
@property (nonatomic, weak) id<NRContextPickerViewControllerDelegate> delegate;
@property (nonatomic, copy) NSString *query;
@end
