
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "ChatControllerDelegate.h"
#import "NREntitiesProvider.h"
#import "HistoryProvider.h"
#import "ContinuityProvider.h"
#import "AccountProvider.h"
#import "ChatHandler.h"
#import "SpeechReconitionDelegate.h"
#import "ChatConfiguration.h"

/************************************************************/
// MARK: - ChatController
/************************************************************/

@interface ChatController : NSObject

/************************************************************/
// MARK: - Properties
/************************************************************/

/**
 The Entities Provider Handles Users Private Information.
 */
@property (nonatomic, weak) id<NREntitiesProvider> entitiesProvider;

/**
 The History Provider For Controlling Chat History.
 */
@property (nonatomic, weak) id<HistoryProvider> historyProvider;

/**
 The Continuity Provider For Stor/ Fetch of Chat Continuity Credentials.
 */
@property (nonatomic, weak) id<ContinuityProvider> continuityProvider;

/**
 The Accoutn Provider
 */
@property (nonatomic, weak) id<AccountProvider> accountProvider;

/**
 The Live Chat Handler (Not Bold, 3rd party lib)
 */
@property (nonatomic, weak) id<ChatHandler> handOver;

/**
 The Chat View Configuration.
 */
@property (nonatomic, strong) ChatConfiguration *viewConfiguration;

/************************************************************/
// MARK: - Delegates
/************************************************************/

/**
 Chat Controller Delegate
 */
@property (nonatomic, weak) id<ChatControllerDelegate> delegate;

/**
 Speech Reconition Delegate
 */
@property (nonatomic, weak) id<SpeechReconitionDelegate> speechReconitionDelegate;

/************************************************************/
// MARK: - Initializer
/************************************************************/

- (instancetype)initWithAccount:(Account *)account;

/************************************************************/
// MARK: - Functions
/************************************************************/

/**
 Helps To Resend Element That Faild.

 @param elements The Element to Resend.
 */
- (void)repostStatements:(NSArray<StorableChatElement> *)elements;


/**
 Ends current chat handler.
 */
- (void)endChat;

/**
 Teminates the chat view.
 */
- (void)terminate;

@end
