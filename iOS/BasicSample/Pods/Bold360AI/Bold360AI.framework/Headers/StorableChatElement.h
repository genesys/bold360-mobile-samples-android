
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import "AgentType.h"
#import "ChatElement.h"
#import "StatementStatus.h"

/************************************************************/
// MARK: - StorableChatElement
/************************************************************/

@protocol StorableChatElement <ChatElement>

/************************************************************/
// MARK: - Properties
/************************************************************/

/**
 The chat elements converted to bytes
 */
@property (nonatomic, copy, readonly) NSString *storageKey;
/**
 The current agent type.
 */
@property (nonatomic, assign) AgentType agentType;
/**
 The current statement status.
 */
@property (nonatomic, assign) StatementStatus status;

@end
