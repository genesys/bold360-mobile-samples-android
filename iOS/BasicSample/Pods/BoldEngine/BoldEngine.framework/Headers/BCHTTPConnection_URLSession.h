
// NanorepUI version number: v2.3.6.rc2 

//
//  BCHTTPConnection_URLSession.h
//  VisitorSDK
//
//  Created by Viktor Fabian on 3/27/14.
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import "BCHTTPConnection.h"

/**
 HTTP connection implemented through NSURLSession. It should be used from iOS 7.0 and above.
 @since Version 1.0
 */
@interface BCHTTPConnection_URLSession : BCHTTPConnection

/**
 Commonly shared url session for REST calls.
 @since Version 1.0
 */
@property(nonatomic, strong)NSURLSession *urlSession;

@end
