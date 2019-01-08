
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import <BoldAIAccessibility/BoldAIAccessibility.h>

/************************************************************/
// MARK: - NRConversationSearchViewDelegate
/************************************************************/

@protocol NRConversationSearchViewDelegate <NSObject>

/**
 Text was submitted.

 @param text content text
 */
- (void)didSubmitText:(NSString *)text;
- (void)focusWebView;

/**
 Speech Recognition Did Fail.
 */
- (void)speechRecognitionDidFailWithStatus:(NRSpeechRecognizerAuthorizationStatus)status;
@end

/************************************************************/
// MARK: - NRConversationSearchView
/************************************************************/

@interface NRConversationSearchView : UIView
@property (nonatomic) BOOL withSpeech;
@property (nonatomic) BOOL withUserInteraction;
@property (nonatomic, copy, readonly) NSString *text;
@property (nonatomic, weak) id<NRConversationSearchViewDelegate> delegate;
- (void)reset;
- (void)softReset;
@end
