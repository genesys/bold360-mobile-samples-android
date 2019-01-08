
// NanorepUI version number: v2.3.6.rc2 

//
//  BCOSSLink.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/28/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BCOSSLink;
@class BCMessage;
@class BCOSSResponsePreProcessor;
@class BCPerson;
@class BCConnectivityManager;

/** @file */
typedef enum {
    BCOSSLinkEndReasonUnknown,
    BCOSSLinkEndReasonOperator,
    BCOSSLinkEndReasonVisitor,
    BCOSSLinkEndReasonDisconnect,
    BCOSSLinkEndReasonClosed,
    BCOSSLinkEndReasonTimeout
} BCOSSLinkEndReason;

@protocol BCOSSLinkDelegate <NSObject>
- (void)ossLinkDidSucceedToConnect:(BCOSSLink *)ossLink;
- (void)ossLink:(BCOSSLink *)ossLink didFailToConnectWithError:(NSError *)error;

- (void)ossLink:(BCOSSLink *)ossLink didReceivePerson:(BCPerson *)person typing:(BOOL)typing;
- (void)ossLink:(BCOSSLink *)ossLink didAcceptChat:(NSString *)acceptTime;
- (void)ossLink:(BCOSSLink *)ossLink didReceiveMessage:(BCMessage *)message;
- (void)ossLink:(BCOSSLink *)ossLink didReceiveAutoMessage:(BCMessage *)message;

- (void)ossLink:(BCOSSLink *)ossLink didReceiveBusyWithPosition:(NSInteger)position unavailableFormAvailable:(BOOL)unavailableFormAvailable;
- (void)ossLink:(BCOSSLink *)ossLink didEndWithReason:(BCOSSLinkEndReason)reason time:(NSDate *)date error:(NSError *)error;

- (void)ossLinkDidReset:(BCOSSLink *)ossLink;

- (void)ossLink:(BCOSSLink *)ossLink didSendMessageId:(NSString *)messageId;
- (void)ossLink:(BCOSSLink *)ossLink didSendTyping:(BOOL)typing;
- (void)ossLink:(BCOSSLink *)ossLink didReceiveLastMessageId:(long long)lastMessageId;

@end


@interface BCOSSLink : NSObject {
    NSInteger _timeoutInSeconds;
}

@property(nonatomic, assign)id<BCOSSLinkDelegate> delegate;
@property(nonatomic, strong)BCOSSResponsePreProcessor *responsePreProcessor;

@property(nonatomic, assign)long long lastMessageId;
@property(nonatomic, strong)NSOperationQueue *operationQueue;
@property(nonatomic, strong)NSString *chatKey;
@property(nonatomic, strong)NSString *clientId;
@property(nonatomic, assign)NSInteger timeoutInSeconds;
@property(nonatomic, assign)BCConnectivityManager *connectivityManager;
@property(nonatomic, assign)NSTimeInterval lastMessageTime;


- (void)start;
- (void)close;
- (void)sendMessage:(BCMessage *)message;
- (void)sendTyping:(BOOL)typing;

- (void)suspend;
- (void)resume;

@end
