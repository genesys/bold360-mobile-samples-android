
// NanorepUI version number: v3.2.0.rc4 

//
//  SpeechReconitionDelegate.h
//  NanorepUI
//
//  Created by Nissim Pardo on 28/08/2018.
//

#import <Foundation/Foundation.h>
#import <BoldAIAccessibility/NRSpeechRecognizerAuthorizationStatus.h>

@protocol SpeechReconitionDelegate <NSObject>
- (void)speechRecognitionStatus:(NRSpeechRecognizerAuthorizationStatus)status;
@end
