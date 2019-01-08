
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCChat.h"
#import "BCConnectivityManager.h"

@class BCPerson;
@class BCForm;

/**
 * @brief The class that implements the \link BCChat \endlink protocol. It is instantiated by the chat session.
 * @since Version 1.0
 */
@interface BCChatImpl : NSObject <BCChat>

/**
 * @brief The delegate array of \link BCChatTyperDelegate \endlink classes.
 * @since Version 1.0
 */
@property(nonatomic, strong)NSMutableArray *chatTyperDelegates;

/**
 * @brief The delegate array of \link BCChatMessageDelegate \endlink classes.
 * @since Version 1.0
 */
@property(nonatomic, strong)NSMutableArray *chatMessageDelegates;

/**
 * @brief The delegate array of \link BCChatQueueDelegate \endlink classes.
 * @since Version 1.0
 */
@property(nonatomic, strong)NSMutableArray *chatQueueDelegates;

/**
 * @brief The delegate array of \link BCChatStateDelegate \endlink classes.
 * @since Version 1.0
 */
@property(nonatomic, strong)NSMutableArray *chatStateDelegates;

/**
 * @brief The language string.
 * @since Version 1.0
 */
@property(nonatomic, strong)NSString *language;

/**
 * @brief The constructor if the \link BCChatImpl::startChat startChat \endlink is not needed to be called on start, and its result is available(long poll url, websocket url, client timeout).
 * @since Version 1.0
 * @param chatId The chat ID.
 * @param chatKey The chat key.
 * @param clientId The client ID.
 * @param visitor The visitor who chats.
 * @param connectivityManager The connectivity manager managing remote calls.
 * @param webSocketUrl The URL the websocket needs to connect to.
 * @param longPollUrl The URL the chat server notifications can be polled.
 * @param clientTimeout The timeout of the oss connections.
 * @returns A BCChatImpl object.
 * @since Version 1.0
 */
- (id)initWithChatId:(NSString *)chatId chatKey:(NSString *)chatKey clientId:(NSString *)clientId visitor:(BCPerson *)visitor connectivityManager:(BCConnectivityManager *)connectivityManager webSocketUrl:(NSString *)webSocketUrl longPollUrl:(NSString *)longPollUrl clientTimeout:(NSInteger)clientTimeout answerTimeout:(NSInteger)answerTimeout;

/**
 * @brief Start the chat.
 * @since Version 1.0
 */
- (void)startChat;

/**
 * @brief Suspend the current chat. The chat session calls it.
 * @since Version 1.0
 */
- (void)suspend;

/**
 * @brief Resume the current chat. The chat session calls it.
 * @since Version 1.0
 */
- (void)resume;
@end
