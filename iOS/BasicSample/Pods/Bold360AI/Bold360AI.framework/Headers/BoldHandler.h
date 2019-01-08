
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// NanorepUI SDK.
// All rights reserved.
// ===================================================================================================

#import <Bold360AI/ChatHandler.h>
#import <BoldEngine/BoldEngine.h>
#import <Bold360AI/ContentChatElement.h>

static NSString * const VisitorID = @"VisitorID";

@interface BoldHandler : NSObject<ChatHandler>
@property (nonatomic, strong) id<BCCancelable> createChatCancelable;
@property (nonatomic, strong) id<BCChatSession> chatSession;

- (void)presentMsg:(NSString *)msg designType:(NSString *)designType;
- (void)presentSystemMsg:(NSString *)msg;

@end
