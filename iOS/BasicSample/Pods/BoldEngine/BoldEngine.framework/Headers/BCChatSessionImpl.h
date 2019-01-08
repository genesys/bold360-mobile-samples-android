
// NanorepUI version number: v2.3.6.rc2 

//
//  BCChatSessionImpl.h
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCChatSession.h"
#import "BCChat.h"
#import <BoldEngine/BCPerson.h>
#import "BCConnectivityManager.h"
#import <BoldEngine/BCCreateChatSessionDelegate.h>

@class BCChatSessionImpl;

/**
 * @brief The delegate callback from session on creation result.
 * @since Version 1.0
 */
@protocol BCChatSessionImplCreationDelegate <NSObject>

/**
 * @brief The chat was created without having pre chat.
 * @param chatSession The created chat session.
 * @since Version 1.0
 */
- (void)bcChatSessionImplDidCreateWithoutPreChat:(BCChatSessionImpl *)chatSession;

/**
 * @brief The chat was created with having pre chat.
 * @param chatSession The created chat session.
 * @param preChat The pre chat form description.
 * @since Version 1.0
 */
- (void)bcChatSessionImpl:(BCChatSessionImpl *)chatSession didCreateWithPreChat:(BCForm *)preChat;

/**
 * @brief The chat is currently unavailable
 * @param chatSession The created chat session.
 * @param reason  The reason for chat to be unavailable.
 * @param unavailableForm The unavailable chat form description.
 * @since Version 1.0
 */
- (void)bcChatSessionImpl:(BCChatSessionImpl *)chatSession didCreateUnavailableWithReason:(BCUnavailableReason)reason unavailableForm:(BCForm *)unavailableForm unavailableMessage:(NSString *)message;

/**
 * @brief The chat failed to be created.
 * @param chatSession The created chat session.
 * @param error The error.
 * @since Version 1.0
 */
- (void)bcChatSessionImpl:(BCChatSessionImpl *)chatSession didFailToCreateWithError:(NSError *)error;
@end

/**
 * @brief The class that implements the \link BCChatSession \endlink protocol. It is instantiated by \link BCAccount \endlink.
 * @since Version 1.0
 */
@interface BCChatSessionImpl : NSObject <BCChatSession>

/**
 * @brief Static constructor.
 * @param accountId The account ID of the \link BCAccount \endlink that creates it.
 * @param accessKey The access key of the \link BCAccount \endlink that creates it.
 * @param connectivityManager The connectivity manager that creates the remote calls for both the chat session and the operator availability checker.
 * @param language The current language.
 * @param visitorId The id of a previous visitor. If omitted, a new one is generated.
 * @param skipPreChat If there is pre-chat set for the chat it can be skipped with sending the answers in the data.
 * @param data The answers for the skipped pre chat and external parameters.
 * @param securedParams An encrypted list of parameters that validate the caller of the API.
 * @returns An instance of BCChatSessionImpl.
 * @since Version 1.1
 */
+ (id)chatSessionImplWithAccountId:(NSString *)accountId
                         accessKey:(NSString *)accessKey
               connectivityManager:(BCConnectivityManager *)connectivityManager
                          language:(NSString *)language
                         visitorId:(NSString *)visitorId
                       skipPreChat:(BOOL)skipPreChat
                              data:(NSDictionary *)data
                     securedParams:(NSString *)securedParams;

/**
 * @brief Constructor.
 * @param accountId The account ID of the BCAccount that creates it.
 * @param accessKey The access key of the BCAccount that creates it.
 * @param connectivityManager The connectivity manager that creates the remote calls for both the chat session and the operator availability checker.
 * @param language The current language.
 * @param visitorId The id of a previous visitor. If omitted, a new one is generated.
 * @param skipPreChat If there is pre chat set for the chat it can be skipped with sending the answers in the data.
 * @param data The answers for the skipped pre chat and external parameters.
 * @param securedParams An encrypted list of parameters that validate the caller of the API.
 * @returns An instance of BCChatSessionImpl.
 * @since Version 1.1
 */
- (id)initWithAccountId:(NSString *)accountId
              accessKey:(NSString *)accessKey
    connectivityManager:(BCConnectivityManager *)connectivityManager
               language:(NSString *)language
              visitorId:(NSString *)visitorId
            skipPreChat:(BOOL)skipPreChat
                   data:(NSDictionary *)data
          securedParams:(NSString *)securedParams;

/**
 * @brief Creates the current session. The result is called back on the delegate.
 * @param createChatDelegate The delegate for calling back.
 * @since Version 1.0
 */
- (void)createChatWithDelegate:(id<BCChatSessionImplCreationDelegate>)createChatDelegate;

@end
