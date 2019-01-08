
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import "ExtraData.h"
@protocol NanorepPersonalInfoHandler <NSObject>

- (void)personalInfoWithExtraData:(NSDictionary *)extraData channel:(NRChanneling *)channel completionHandler:(void(^)(NSDictionary *formData))handler;
- (void)didFetchExtraData:(ExtraData *)formData;
- (void)didSubmitForm;
- (void)didCancelForm;
- (void)didFailSubmitFormWithError:(NSError *)error;
- (BOOL)shouldOverridePhoneChannel:(NRChannelingPhoneNumber *)phoneChannel;
- (void)didSubmitFeedback:(NSDictionary *)info;

@optional

/**
 Enable / Disable Popup confirmation

 @param channel The channel which should be presented.
 @return true for presenting the popup (If method won't be implemented the default will be true)
 */
- (BOOL)shouldPresentConfirmationPopupForChannel:(NRChanneling *)channel;
@end
