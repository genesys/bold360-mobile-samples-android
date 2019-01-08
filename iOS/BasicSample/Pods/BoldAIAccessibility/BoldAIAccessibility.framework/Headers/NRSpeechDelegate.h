
// NanorepUI version number: v1.6.1.rc1 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// BoldAIAccessibility SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

@protocol NRSpeechDelegate <NSObject>
- (void)recoredDidEnd;
- (void)speechDetected:(NSString *)text;
@end
