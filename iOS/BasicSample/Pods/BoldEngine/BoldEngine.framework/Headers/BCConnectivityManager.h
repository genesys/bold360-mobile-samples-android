
// NanorepUI version number: v2.3.6.rc2 

//
//  BCURLConnectionManager.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/27/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BCCreateChatCall;
@class BCStartChatCall;
@class BCFinishChatCall;
@class BCOSSCommunicator;
@class BCLongPollCall;
@class BCSendMessageCall;
@class BCVisitorTypingCall;
@class BCGetChatAvailabilityCall;
@class BCSubmitUnavailableEmailCall;
@class BCSubmitPreChatCall;
@class BCSubmitPostChatCall;
@class BCEmailChatHistoryCall;
@class BCPingChatCall;
@class BCGetUnavailableFormCall;
@class BCChangeLanguageCall;

/**
 Connectivity manager that creates the REST calls and the OSS communicator. It also has an operation queue for the delegates of the REST callst and the OSS. It contains an URL session for the REST calls for iOS 7.
 @since Version 1.0
 */
@interface BCConnectivityManager : NSObject

/**
 Operation queue for REST and websocket delegates. The delegates are called back on backgrount threads.
 @since Version 1.0
 */
@property(nonatomic, strong)NSOperationQueue *networkOperationQueue;

/**
 Common URL session for REST calls. It is for iOS 7 and above.
 @since Version 1.0
 */
@property(nonatomic, strong)NSURLSession *urlSession; //only on iOS 7 and later

/**
 Account Id.
 @since Version 1.0
 */
@property(nonatomic, strong)NSString *accountId;

/**
 Access key.
 @since Version 1.0
 */
@property(nonatomic, strong)NSString *accessKey;

/**
 A string that is appended to web- url part.
 @since Version 1.0
 */
@property(nonatomic, copy)NSString *serverSet;

/**
 CreateChat REST call generation.
 @returns A preconfigured instance of BCCreateChatCall.
 @since Version 1.0
 */
- (BCCreateChatCall *)createChatCall;

/**
 StartChat REST call generation.
 @returns A preconfigured instance of BCStartChatCall.
 @since Version 1.0
 */
- (BCStartChatCall *)startChatCall;

/**
 FinishChat REST call generation.
 @returns A preconfigured instance of BCFinishChatCall.
 @since Version 1.0
 */
- (BCFinishChatCall *)finishChatCall;

/**
 Long Poll REST call generation.
 @returns A preconfigured instance of BCLongPollCall.
 @since Version 1.0
 */
- (BCLongPollCall *)longPollCall;

/**
 SendMessage REST call generation.
 @returns A preconfigured instance of BCSendMessageCall.
 @since Version 1.0
 */
- (BCSendMessageCall *)sendMessageCall;

/**
 VisitorTyping REST call generation.
 @returns A preconfigured instance of BCVisitorTypingCall.
 @since Version 1.0
 */
- (BCVisitorTypingCall *)visitorTypingCall;

/**
 GetChatAvailability REST call generation.
 @returns A preconfigured instance of BCGetChatAvailabilityCall.
 @since Version 1.0
 */
- (BCGetChatAvailabilityCall *)getChatAvailabilityCall;

/**
 SubmitUnavailableEmail REST call generation.
 @returns A preconfigured instance of BCSubmitUnavailableEmailCall.
 @since Version 1.0
 */
- (BCSubmitUnavailableEmailCall *)submitUnavailableEmailCall;

/**
 SubmitPreChat REST call generation.
 @returns A preconfigured instance of BCSubmitPreChatCall.
 @since Version 1.0
 */
- (BCSubmitPreChatCall *)submitPreChatCall;

/**
 SubmitPostChat REST call generation.
 @returns A preconfigured instance of BCSubmitPostChatCall.
 @since Version 1.0
 */
- (BCSubmitPostChatCall *)submitPostChatCall;

/**
 EmailChatHistory REST call generation.
 @returns A preconfigured instance of BCEmailChatHistoryCall.
 @since Version 1.0
 */
- (BCEmailChatHistoryCall *)emailChatHistoryCall;

/**
 PingChatCall REST call generation.
 @returns A preconfigured instance of BCPingChatCall.
 @since Version 1.0
 */
- (BCPingChatCall *)pingChatCall;

/**
 GetUnavailableForm REST call generation.
 @returns A preconfigured instance of BCGetUnavailableFormCall.
 @since Version 1.0
 */
- (BCGetUnavailableFormCall *)getUnavailableFormCall;

/**
 ChangeLanguage REST call generation.
 @returns A preconfigured instance of BCChangeLanguageCall.
 @since Version 1.0
 */
- (BCChangeLanguageCall *)changeLanguageCall;

/**
 Creation of an OSS communicator.
 @returns An instance of the OSS communicator.
 @since Version 1.0
 */
- (BCOSSCommunicator *)ossCommunicator;

@end
