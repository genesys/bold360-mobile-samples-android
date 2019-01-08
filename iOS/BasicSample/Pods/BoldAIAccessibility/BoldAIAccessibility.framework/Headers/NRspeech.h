
// NanorepUI version number: v1.6.1.rc1 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// BoldAIAccessibility SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "NRSpeechDelegate.h"
#import "NRSpeechRecognizerAuthorizationStatus.h"

@protocol NRSpeech <NSObject>
@property (nonatomic, copy) NSLocale *locale;
@property (nonatomic, weak) id<NRSpeechDelegate> delegate;
- (void)record;
- (void)requestAuthorization:(void(^)(NRSpeechRecognizerAuthorizationStatus status))handler;
@end
