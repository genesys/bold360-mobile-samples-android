
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

/************************************************************/
// MARK: - StateMachine
/************************************************************/

/// An StateMachine is an enum of different conversation states
typedef NS_ENUM(NSInteger, StateMachine) {
    /// Sent when webViewReady & cnfReady are loading
    pending,
    /// Sent when webViewReady & cnfReady are ready
    ready,
    /// Sent when webViewReady is ready
    webViewReady,
    /// Sent when cnfReady is ready
    cnfReady,
    /// Sent when ConversationState is error
    error
};

/************************************************************/
// MARK: - HistoryStateMachine
/************************************************************/

/// An StateMachine is an enum of different conversation history states
typedef NS_ENUM(NSInteger, HistoryStateMachine) {
    /// Sent when history loading
    loading,
    /// Sent when history loaded
    loaded,
    /// Sent when there is no hsitory
    none
};

/************************************************************/
// MARK: - ConversationStateManager
/************************************************************/

@interface ConversationStateManager : NSObject

/************************************************************/
// MARK: - Properties
/************************************************************/

@property (nonatomic) StateMachine currentState;
@property (nonatomic) HistoryStateMachine currentHistoryState;

/************************************************************/
// MARK: - Methods
/************************************************************/

- (void)updateCurrentState:(StateMachine)state;
- (void)registerStateReady:(void(^)(void))handler;

@end
