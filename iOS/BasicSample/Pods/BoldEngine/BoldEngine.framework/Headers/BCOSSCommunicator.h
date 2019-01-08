
// NanorepUI version number: v2.3.6.rc2 

//
//  BCOSSCommunicator.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/28/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <BoldEngine/BCMessage.h>

@class BCOSSCommunicator;
@class BCPerson;
@class BCConnectivityManager;

/** @file */
typedef enum {
    BCOSSCommunicatorEndReasonUnknown,
    BCOSSCommunicatorEndReasonOperator,
    BCOSSCommunicatorEndReasonVisitor,
    BCOSSCommunicatorEndReasonDisconnect,
    BCOSSCommunicatorEndReasonTimeout,
    BCOSSCommunicatorEndReasonClosed
} BCOSSCommunicatorEndReason;

@protocol BCOSSCommunicatorDelegate <NSObject>
- (void)ossCommunicatorDidSucceedToConnect:(BCOSSCommunicator *)ossCommunicator;
- (void)ossCommunicator:(BCOSSCommunicator *)ossCommunicator didFailToConnectWithError:(NSError *)error;

- (void)ossCommunicator:(BCOSSCommunicator *)ossCommunicator didAcceptChat:(NSString *)acceptTime;
- (void)ossCommunicator:(BCOSSCommunicator *)ossCommunicator didReceivePerson:(BCPerson *)person typing:(BOOL)typing;
- (void)ossCommunicator:(BCOSSCommunicator *)ossCommunicator didReceiveMessage:(BCMessage *)message;
- (void)ossCommunicator:(BCOSSCommunicator *)ossCommunicator didReceiveAutoMessage:(BCMessage *)message;

- (void)ossCommunicator:(BCOSSCommunicator *)ossCommunicator didReceiveBusyWithPosition:(NSInteger)position unavailableFormAvailable:(BOOL)unavailableFormAvailable;
- (void)ossCommunicator:(BCOSSCommunicator *)ossCommunicator didEndWithReason:(BCOSSCommunicatorEndReason)reason time:(NSDate *)date error:(NSError *)error;

- (void)ossCommunicator:(BCOSSCommunicator *)ossCommunicator didSendMessage:(BCMessage *)message;
- (void)ossCommunicator:(BCOSSCommunicator *)ossCommunicator didSendTyping:(BOOL)typing;

- (void)ossCommunicatorDidReset:(BCOSSCommunicator *)ossCommunicator;

@end

@interface BCOSSCommunicator : NSObject

@property(nonatomic, copy)NSString *webSocketURL;
@property(nonatomic, copy)NSString *longPollURL;
@property(nonatomic, copy)NSString *chatKey;
@property(nonatomic, copy)NSString *clientId;
@property(nonatomic, assign)id<BCOSSCommunicatorDelegate> delegate;
@property(nonatomic, assign)BCConnectivityManager *connectivityManager;
@property(nonatomic, strong)NSURLSession *urlSession; //only on iOS 7 and later
@property(nonatomic, assign)NSInteger timeoutInSeconds;
@property(nonatomic, assign)long long lastMessageId;
@property(nonatomic, assign)long long lastChatMessageId;

- (void)start;
- (void)close;
- (void)sendMessage:(BCMessage *)message;
- (void)sendTyping:(BOOL)typing;

- (void)suspend;
- (void)resume;

- (NSUInteger)countOfUnsentMessages;
@end
