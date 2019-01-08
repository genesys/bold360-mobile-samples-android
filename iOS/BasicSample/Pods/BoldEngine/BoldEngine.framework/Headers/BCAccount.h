
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <BoldCore/Account.h>

@protocol BCChatSession;
@protocol BCCancelable;
@protocol BCChatAvailabilityDelegate;
@protocol BCCreateChatSessionDelegate;

/** @file */

/**
 * @brief A set of BoldChat account settings identified by an access key.
 * @details <p>This class is the starting point for access the BoldChat SDK. To begin an instance of this class should be created with \link BCAccount::accountWithAccessKey: \endlink or \link BCAccount::initWithAccessKey: \endlink.
 * This class instantiates the chat availability checkers and the chat sessions. </p><p>The chat availability checkers check if the chat is available. These are created by \link BCAccount::getChatAvailabilityWithDelegate: getChatAvailabilityWithDelegate:\endlink and \link BCAccount::getChatAvailabilityWithDelegate:visitorId: getChatAvailabilityWithDelegate:visitorId: \endlink calls.</p>
 * <p>The chat sessions are for performing the actual chat session. They are created by \link BCAccount::createChatSessionWithDelegate:language: createChatSessionWithDelegate:language:\endlink and \link BCAccount::createChatSessionWithDelegate:language:visitorId:skipPreChat:externalParams:\endlink.</p>
 * @since Version 1.0
 */
@interface BCAccount : Account

/**
 * @brief The access key for the SDK.
 * @since Version 1.0
 */
@property (nonatomic, copy, readonly) NSString *accessKey;

/** 
 * @brief The static constructor.
 * @param accessKey Access key. Mandatory parameter.
 * @returns A BCAccount instance.
 * @since Version 1.0
 */
+ (id)accountWithAccessKey:(NSString *)accessKey;

/**
 * @brief The constructor.
 * @param accessKey Access key. Mandatory parameter.
 * @returns A BCAccount instance.
 * @since Version 1.0
 */
- (id)initWithAccessKey:(NSString *)accessKey;

/**
 * @brief Initiates the chat session creation process. If pre chat form is enabled it will return pre chat information, if pre chat is not enabled it will return information about connecting the chat. If chat is unavailable then unavailable information will be returned instead. Calling this method is required for all chat sessions.
 * @param delegate Delegate The delegate that will be called to receive the data after the SDK call is completed.
 * @param language Language string to initialize. It must be an ISO 639-1 language code optionally followed by a dash then an ISO 3166-1 country code (en-US).If not set, the application's current language is going to be set.
 * @returns An object that implements BCCancelable protocol to be able to cancel the request.
 * @since Version 1.0
 */
- (id<BCCancelable>)createChatSessionWithDelegate:(id<BCCreateChatSessionDelegate>)delegate language:(NSString *)language;

/**
 * @brief Initiates the chat session creation process. If pre chat form is enabled it will return pre chat information, if pre chat is not enabled it will return information about connecting the chat. If chat is unavailable then unavailable information will be returned instead. Calling this method is required for all chat sessions.
 * @param delegate Delegate The delegate that will be called to receive the data after the SDK call is completed.
 * @param language Language string to initialize. It must be an ISO 639-1 language code optionally followed by a dash then an ISO 3166-1 country code (en-US). If not set, the application's current language is going to be set.
 * @param visitorId The id If the application has previously called \link BCAccount::createChatSessionWithDelegate:language: createChatSession \endlink and a visitorId was returned re-use that id for all subsequent calls for each unique user. If this is the first time the application has used createChat this parameter should be left empty and a new VisitorID will be generated and returned by the server.
 * @param skipPreChat If there is pre chat set for the chat it can be skipped with sending the answers in the data.
 * @param externalParams The answers for the skipped pre chat and external parameters.
 * @returns An object that implements BCCancelable protocol to be able to cancel the request.
 * @since Version 1.0
 */
- (id<BCCancelable>)createChatSessionWithDelegate:(id<BCCreateChatSessionDelegate>)delegate language:(NSString *)language visitorId:(NSString *)visitorId skipPreChat:(BOOL)skipPreChat externalParams:(NSDictionary *)externalParams;

/**
 * @brief Initiates the chat session creation process. If pre-chat form is enabled it will return pre-chat information, if pre-chat is not enabled it will return information about connecting the chat. If chat is unavailable then unavailable information will be returned instead. Calling this method is required for all chat sessions.
 * @param delegate Delegate The delegate that will be called to receive the data after the SDK call is completed.
 * @param language Language string to initialise. It must be an ISO 639-1 language code optionally followed by a dash then an ISO 3166-1 country code (en-US).If not set, the application's current language is going to be set.
 * @param securedParams An encrypted list of parameters that validate the caller of the API.
 * @returns An object that implements BCCancelable protocol to be able to cancel the request.
 * @since Version 1.1
 */
- (id<BCCancelable>)createSecuredChatSessionWithDelegate:(id<BCCreateChatSessionDelegate>)delegate language:(NSString *)language securedParams:(NSString *)securedParams;

/**
 * @brief Initiates the chat session creation process. If pre-chat form is enabled it will return pre-chat information, if pre-chat is not enabled it will return information about connecting the chat. If chat is unavailable then unavailable information will be returned instead. Calling this method is required for all chat sessions.
 * @param delegate Delegate The delegate that will be called to receive the data after the SDK call is completed.
 * @param language Language string to initialise. It must be an ISO 639-1 language code optionally followed by a dash then an ISO 3166-1 country code (en-US).If not set, the application's current language is going to be set.
 * @param visitorId The id If the application has previously called createChatSession and a visitorId was returned re-use that id for all subsequent calls for each unique user. If this is the first time the application has used createChat this parameter should be left empty and a new VisitorID will be generated and returned by the server.
 * @param skipPreChat If there is pre-chat set for the chat it can be skipped with sending the answers in the data
 * @param externalParams The answers for the skipped pre chat and external parameters.
 * @param securedParams An encrypted list of parameters that validate the caller of the API.
 * @returns An object that implements BCCancelable protocol to be able to cancel the request.
 * @since Version 1.1
 */
- (id<BCCancelable>)createSecuredChatSessionWithDelegate:(id<BCCreateChatSessionDelegate>)delegate language:(NSString *)language visitorId:(NSString *)visitorId skipPreChat:(BOOL)skipPreChat externalParams:(NSDictionary *)externalParams  securedParams:(NSString *)securedParams;





/** 
 * @brief Retrieves the availability of the chat service.  This can be used to check if chat will be available before showing the option for live chat. This can
  be called at any time without an existing chat session. Results will be cached for 1 minute.
 * @param delegate The delegate that will be called with the chat availability.
 * @returns An object that implements \link BCCancelable \endlink protocol to be able to cancel the request.
 * @since Version 1.0
 */
- (id<BCCancelable>)getChatAvailabilityWithDelegate:(id<BCChatAvailabilityDelegate>)delegate;

/** 
 * @brief Retrieves the availability of the chat service.  This can be used to check if chat will be available before showing the option for live chat. This can be called at any time without an existing chat session. Results will be cached for 1 minute.
 * @param delegate The delegate that will be called with the chat availability.
 * @param visitorId The id of a visitor that used the chat previously. It can be nil.
 * @returns An object that implements \link BCCancelable \endlink protocol to be able to cancel the request.
 * @since Version 1.0
 */
- (id<BCCancelable>)getChatAvailabilityWithDelegate:(id<BCChatAvailabilityDelegate>)delegate visitorId:(NSString *)visitorId;

/** 
 * @brief The SDK version string.
 * @returns Version string.
 * @since Version 1.0
 */
- (NSString *)versionString;

@end

/** Language of the visitor (e.g., en-US). If the window does not support the language, it will fallback to the default language of the window. String value required. @since Version 1.0 */
extern NSString *const BCFormFieldLanguage;

/** The id of the department of the chat.  String value required. @since Version 1.0 */
extern NSString *const BCFormFieldDepartment;

/** The name of the visitor. String value required. @since Version 1.0 */
extern NSString *const BCFormFieldFirstName;

/** The name of the visitor (synonymous with first_name). String value required. @since Version 1.0 */
extern NSString *const BCFormFieldName;

/** The last name of the visitor. String value required. @since Version 1.0 */
extern NSString *const BCFormFieldLastName;

/** The phone number of the visitor. String value required. @since Version 1.0 */
extern NSString *const BCFormFieldPhone;

/** The email of the visitor. String value required. @since Version 1.0 */
extern NSString *const BCFormFieldEmail;

/** The initial question for the chat (which will show as the first chat message in the chat from the visitor). String value required. @since Version 1.0 */
extern NSString *const BCFormFieldInitialQuestion;

/** The visitor reference value that appears in the client. String value required. @since Version 1.0 */
extern NSString *const BCFormFieldReference;

/** The visitor info value that appears in the client. String value required. @since Version 1.0 */
extern NSString *const BCFormFieldInformation;

/** Survey overall response value. String with integer value required. @since Version 1.0 */
extern NSString *const BCFormFieldOverall;

/** Survey knowledge response value. String with integer value required. @since Version 1.0 */
extern NSString *const BCFormFieldKnowledge;

/** Survey responsiveness response value. String with integer value required. @since Version 1.0 */
extern NSString *const BCFormFieldResponsiveness;

/** Survey professionalism response value. String with integer value required. @since Version 1.0 */
extern NSString *const BCFormFieldProfessionalism;

/** Survey comments response value. String value required. @since Version 1.0 */
extern NSString *const BCFormFieldComments;

/** Custom url parameter @since Version 1.1 */
extern NSString *const BCFormFieldCustomUrl;

/** custom_[name] Sets the custom field (either pre or post chat) with the given name to the given value. String value required. @since Version 1.0 */
#define BCFormFieldComments(name) ([NSString stringWithFormat:@"custom_%@",name])

