
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

/** @file */
/**
 * @brief List of generic errors.
 * @since Version 1.0
 */
typedef enum {
    BCGeneralUnknownError = -100, /**< An unknown error happened. @since Version 1.0 */
    BCGeneralNetworkError = -101, /**< There was error in connecting to the servers. @since Version 1.0 */
    BCGeneralFormattingError = -102, /**< A malformatted message came from the server. @since Version 1.0 */
    BCGeneralInvalidAccessKeyError = -103, /**< The access key, that is used is invalid. @since Version 1.0 */
    BCGeneralTimeoutError = -104, /**< Connecting to servers timed out. @since Version 1.0 */
}BCGeneralError;

/**
 * @brief Error codes initiated from the BCChatSession.
 * @since Version 1.0
 */
typedef enum {
    BCChatSessionErrorInvalidChatKey = -200, /**< The chat key required by the backend API calls is invalid. @since Version 1.0 */
    BCChatSessionErrorInvalidVisitorId = -201, /**< The visitor ID is invalid. @since Version 1.0 */
    BCChatSessionErrorInvalidDepartmentId = -202, /**< The department ID is invalid. @since Version 1.0 */
    BCChatSessionErrorInvalidState  = -203, /**< The request was called in not a correct chat session state. Like pre chat form answers are sent while chatting. @since Version 1.0 */
    BCChatSessionErrorRequiredFieldMissing = -204, /**< An answer value of a required form field is not filled. @since Version 1.0 */
    BCChatSessionErrorInvalidEmailFormat = -205, /**< The email address is given in invalid format. @since Version 1.0 */
    BCChatSessionErrorInvalidPhoneFormat = -206, /**< The phone number is given in invalid format. @since Version 1.0 */
} BCChatSessionError;

/**
 * @brief Error codes initiated from the BCChat.
 * @since Version 1.0
 */
typedef enum {
    BCChatErrorFailedToStart = -300, /**< Starting chat failed. @since Version 1.0 */
    BCChatErrorFailedToFinish = -301,/**< Finishing chat failed. @since Version 1.0 */
    BCChatErrorFailedToGetUnavailableForm = -302,/**< Getting the unavailable chat form failed. @since Version 1.0 */
    BCChatErrorOSSConnection = -303,/**< Connection to the chat servers failed. @since Version 1.0 */
}BCChatError;
