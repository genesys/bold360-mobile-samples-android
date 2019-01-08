
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "NRQuickOption.h"
#import "StorableChatElement.h"
#import "BoldForm.h"

/**
 An ChatState is an enum of different chat states in chat lifecycle.
 */
typedef NS_ENUM(NSInteger, ChatState) {
    /// Chat started.
    ChatStarted,
    /// Chat ended.
    ChatEnded
};

@protocol ChatControllerDelegate <NSObject>

- (void)shouldPresentChatViewController:(UIViewController *)viewController;
- (void)didFailLoadChatWithError:(NSError *)error;

@optional
- (BOOL)shouldHandleFormPresentation:(UIViewController *)formController;
- (void)statement:(id<StorableChatElement>)statement didFailWithError:(NSError *)error;
- (BOOL)presentingController:(UIViewController *)controller shouldHandleClickedLink:(NSString *)link;
- (void)didClickToCall:(NSString *)phoneNumber;
- (void)didClickLink:(NSString *)url;
- (void)didClickApplicationQuickOption:(NRQuickOption *)quickOption;
- (BOOL)shouldPresentWelcomeMessage;

/**
 Updates when chat state was changed (chat lifecycle).

 @param state The chat state (started/ ended).
 @param agentType The agent type (bot/ live).
 */
- (void)didUpdateState:(ChatState)state withType:(AgentType)agentType;

/**
 Triggred when form should be presented.
 
 @param form The form data object.
 @param completionHandler The handler with custome view controller to be presented.
 */
- (void)shouldPresentForm:(BrandedForm *)form handler:(void (^)(UIViewController<BoldForm> *vc))completionHandler;

@end
