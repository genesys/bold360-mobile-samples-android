
// NanorepUI version number: v2.3.6.rc2 

//
//  BCRESTCall.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/27/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCHTTPConnection.h"
#import "BCRESTCallResultParser.h"

@class BCRESTCall;

/**
 Delegate for BCRESTCall.
 @since Version 1.0
 */
@protocol BCRESTCallDelegate <NSObject>

/**
 Positive callback with the result that is the output of the parser.
 @param restCall The REST call the delegate was called back by.
 @param result The result object of the call.
 @since Version 1.0
 */
- (void)bcRestCall:(BCRESTCall *)restCall didFinishWithResult:(NSObject *)result;

/**
 Positive callback with the result that is the output of the parser.
 @param restCall The REST call the delegate was called back by.
 @param error The error, returned by the call.
 @since Version 1.0
 */
- (void)bcRestCall:(BCRESTCall *)restCall didFinishWithError:(NSError *)error;

@end

/**
 A class for REST calls. It handles the underlying connection, parsing, running the paring on a background task.
 @since Version 1.0
 */
@interface BCRESTCall : NSObject

/**
 The delegate.
 @since Version 1.0
 */
@property(assign) id<BCRESTCallDelegate> delegate;

/**
 Account ID.
 @since Version 1.0
 */
@property(copy) NSString *accountId;

/**
 Access key.
 @since Version 1.0
 */
@property(copy) NSString *accessKey;

/**
 The name of the REST call.
 @since Version 1.0
 */
@property(copy) NSString *methodName;

/**
 The parameters for the REST call.
 @since Version 1.0
 */
@property(copy) NSDictionary *params;

/**
 Result parser from NSData to an object.
 @since Version 1.0
 */
@property(strong) BCRESTCallResultParser *parser;

/**
 If YES, the call has no timeout. It is good for long polling.
 @since Version 1.0
 */
@property(assign)BOOL infiniteTimeout;

/**
 Set if the URL to call is not the general URL for BoldChat REST calls.
 @since Version 1.0
 */
@property(nonatomic, copy)NSString *customUrl;

/**
 A string that is appended to web- url part.
 @since Version 1.0
 */
@property(nonatomic, copy)NSString *serverSet;

/**
 Static constructor.
 @param connection The connection to run the request.
 @since Version 1.0
 */
+ (id)restCallWithHttpConnection:(BCHTTPConnection *)connection;

/**
 Constructor.
 @param connection The connection to run the request.
 @since Version 1.0
 */
- (id)initWithHttpConnection:(BCHTTPConnection *)connection;


/**
 Start the request. The result is called back on the delegate on the main thread.
 @since Version 1.0
 */
- (void)start;

/**
 Stop the request. There are no callbacks on the delegate after it.
 @since Version 1.0
 */
- (void)cancel;

/**
 Suspends the call. Does not cancel the call, just cancels the timeout timer.
 @since Version 1.0
 */
- (void)suspend;

/**
 Restarts the timeout
 @since Version 1.0
 */
- (void)resume;



@end
