
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, AlertSelection) {
    AlertSelectionCancel,
    AlertSelectionIncorrectAnswer,
    AlertSelectionWrongInformation,
    AlertSelectionIrrelevant
};

@interface NRAlertViewController : UIViewController
@property (nonatomic, copy) void(^completion)(AlertSelection selection);
@property (nonatomic, copy) NSDictionary *localeDict;
@end
