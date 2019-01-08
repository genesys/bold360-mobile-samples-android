
// NanorepUI version number: v2.3.6.rc2 

//
//  BCHTTPConnection.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/27/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BCHTTPConnection;

/** @file */
/**
 HTTP connection error reasons.
 @since Version 1.0
 */
typedef enum {
    BCHTTPConnectionErrorTimeout = -1, //**< The connection timed out. @since Version 1.0*/
    BCHTTPConnectionErrorConnection = -2, //**< There was a network error.@since Version 1.0*/
} BCHTTPConnectionError;

/**
 Delegate of BCHTTPConnection.
 @since Version 1.0
 */
@protocol BCHTTPConnectionDelegate <NSObject>

/**
 Successful data retreival.
 @param connection The connection that the delegate is called by.
 @param request The initial request.
 @param data The retreived data.
 @since Version 1.0
 */
- (void)bcHttpConnection:(BCHTTPConnection *)connection request:(NSURLRequest *)request didSucceedWithData:(NSData *)data;

/**
 Failure in the connection.
 @param connection The connection that the delegate is called by.
 @param request The initial request.
 @param error The error of the connection. Codes are listed in BCHTTPConnectionError.
 @since Version 1.0
 */
- (void)bcHttpConnection:(BCHTTPConnection *)connection request:(NSURLRequest *)request didFailWithError:(NSError *)error;
@end

/**
 A base class for HTTP connections.
 @since Version 1.0
 */
@interface BCHTTPConnection : NSObject {
    /**
     The delegate ivar.
     @since Version 1.0
     */
    id<BCHTTPConnectionDelegate> _delegate;
    
    /**
     Synchronasation object for connection handling and delegate callbacks on a separate thread.
     @since Version 1.0
     */
    NSObject *_syncObject;
}

/**
 The delegate property. Strong delegate is intentional. Cancelling is needed before releasing the object.
 @since Version 1.0
 */
@property(nonatomic, strong) id<BCHTTPConnectionDelegate> delegate;

/**
 The operation queue for running the delegate calls.
 @since Version 1.0
 */
@property(nonatomic, strong) NSOperationQueue *operationQueue;

/**
 The currently running HTTP request.
 @since Version 1.0
 */
@property(nonatomic, strong) NSURLRequest *request;

/**
 Constructor.
 @param urlRequest The request to run.
 @param delegate The delegate.
 @returns An instance of BCHTTPConnection.
 @since Version 1.0
 */
- (id)initWithRequest:(NSURLRequest *)urlRequest delegate:(id<BCHTTPConnectionDelegate>)delegate;

/**
 Start the HTTP request.
 @since Version 1.0
 */
- (void)start;

/**
 Stop the HTTP request.
 @since Version 1.0
 */
- (void)cancel;

@end
