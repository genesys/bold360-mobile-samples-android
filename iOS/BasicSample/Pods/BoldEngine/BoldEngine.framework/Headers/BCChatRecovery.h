
// NanorepUI version number: v2.3.6.rc2 

//
//  BCChatRecovery.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 4/15/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BCConnectivityManager;
@class BCChatRecovery;

/**
 Chat recovery delegate.
 @since version 1.0
 */
@protocol BCChatRecoveryDelegate <NSObject>
@required
/**
 Recapture state changed.
 @since version 1.0
 */
- (void)bcChatRecovery:(BCChatRecovery *)chatRecovery didReceiveRecaptureAvailable:(BOOL)recaptureAvailable;
@optional

/**
 Close was sent successfully.
 @since version 1.0
 */
- (void)bcChatRecoveryCloseDidFinish:(BCChatRecovery *)chatRecovery;

/**
 Failed to send close.
 @since version 1.0
 */
- (void)bcChatRecovery:(BCChatRecovery *)chatRecovery didFailToSendCloseWithError:(NSError *)error;
@end

/**
 A repetitive checker for available chat recapture.
 @since version 1.0
 */
@interface BCChatRecovery : NSObject

/**
 Delegate.
 @since version 1.0
 */
@property(nonatomic, assign)id<BCChatRecoveryDelegate> delegate;

/**
 Chat key for the chat.
 @since version 1.0
 */
@property(nonatomic, copy)NSString *chatKey;

/**
 Connectivity manager to create REST calls.
 @since version 1.0
 */
@property(nonatomic, strong)BCConnectivityManager *connectivityManager;

/**
 YES if chat recapture is available.
 @since version 1.0
 */
@property(nonatomic, readonly)BOOL recaptureAvailable;

/**
 Start operation.
 @since version 1.0
 */
- (void)start;

/**
 Stop operation.
 @since version 1.0
 */
- (void)stop;

/**
 Send being closed by the user and finish operation on return.
 @since version 1.0
 */
- (void)sendClosedAndStop;
@end
