
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "ChatHandler.h"
#import "HistoryProvider.h"
#import "LocalChatElement.h"
#import "SpeechReconitionDelegate.h"
#import "ChatConfiguration.h"

/************************************************************/
// MARK: - ChatViewController
/************************************************************/

@interface ChatViewController : UIViewController<ChatHandlerDelegate>

/**
 `ChatHandler` used as Bot/ Handover handler
 */
@property (nonatomic, strong) id<ChatHandler> chatHandler;

/**
 `ChatEventHandler` object used to handle events on chat.
 */
@property (nonatomic, strong) id<ChatEventHandler> chatEventHandler;

/**
 `HistoryProvider` object used to manage history.
 */
@property (nonatomic, weak) id<HistoryProvider> historyProvider;

/**
 `SpeechReconitionDelegate` object used to manage speech recognition status.
 */
@property (nonatomic, weak) id<SpeechReconitionDelegate> speechReconitionDelegate;

/**
 `SpeechReconitionDelegate` object used to manage speech recognition status.
 */
@property (nonatomic, strong) ChatConfiguration *config;

@end
