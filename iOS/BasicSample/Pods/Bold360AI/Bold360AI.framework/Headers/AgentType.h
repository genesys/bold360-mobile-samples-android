
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

/************************************************************/
// MARK: - AgentType
/************************************************************/

/// An AgentType is an enum of different agent states
typedef NS_ENUM(NSInteger, AgentType) {
    /// Sent when agent type is bot
    Bot,
    /// Sent when agent type is live
    Live,
    /// Sent when agent type is none
    None = -1
};
