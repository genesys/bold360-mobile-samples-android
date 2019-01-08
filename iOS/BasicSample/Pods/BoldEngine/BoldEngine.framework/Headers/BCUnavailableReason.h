
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

/** @file */
/**
 * @brief The possible reasons for chat to be unavailable.
 * @since Version 1.0
 */
typedef enum {
    BCUnavailableReasonUnknown, /**< Reason unknown. @since Version 1.0 */
    BCUnavailableReasonQueueFull, /**< The number of chats waiting and in progress exceeds the queue size limit. @since Version 1.0 */
    BCUnavailableReasonNoOperators, /**< No operators are logged in as available. @since Version 1.0 */
    BCUnavailableReasonVisitorBlocked, /**< The visitor has been blocked by an operator. @since Version 1.0 */
    BCUnavailableReasonOutsideHours, /**< It is currently outside the operating hours for the given WebsiteID. @since Version 1.0 */
    BCUnavailableReasonUnsecure, /**< Chat requires validation and has not been validated. @since Version 1.0 */
}BCUnavailableReason;
