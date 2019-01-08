
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

/************************************************************/
// MARK: - StatementStatus
/************************************************************/

/// An StatementStatus is an enum of different statemen states
typedef NS_ENUM(NSInteger, StatementStatus) {
    /// Sent when statement response status is ok
    OK,
    /// Sent when statement response status is pending
    Pending,
    /// Sent when statement response status is error
    Error,
    /// Shouldn't be presented
    Repost
};

