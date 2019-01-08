#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "BoldAIAccessibility.h"
#import "NRspeech.h"
#import "NRSpeechDelegate.h"
#import "NRSpeechDetector.h"
#import "NRSpeechRecognizerAuthorizationStatus.h"

FOUNDATION_EXPORT double BoldAIAccessibilityVersionNumber;
FOUNDATION_EXPORT const unsigned char BoldAIAccessibilityVersionString[];

