
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "ChatElement.h"

@interface PreChatInfo : NSObject
@property (nonatomic, assign) BOOL loading;
@property (nonatomic, readwrite) id<ChatElement> welcomeMessage;
@property (nonatomic, copy) NSDictionary *extraInfo;
@end
