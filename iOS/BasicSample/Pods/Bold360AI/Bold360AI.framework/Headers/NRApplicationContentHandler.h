
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "NRQuickOption.h"

@class NRConversationalViewController;
@protocol NRApplicationContentHandler <NSObject>
- (BOOL)presentingController:(UIViewController *)controller shouldHandleClickedLink:(NSString *)link;
- (void)didClickToCall:(NSString *)phoneNumber;
- (void)didClickLink:(NSString *)url;
- (void)didSessionExpire;
- (void)controller:(NRConversationalViewController *)controller didClickApplicationQuickOption:(NRQuickOption *)quickOption;
@end
