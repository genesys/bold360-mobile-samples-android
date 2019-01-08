
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import "RemoteChatElement.h"
#import <BoldAIEngine/NRConversationalResponse.h>

@interface RemoteChatElement (NRConversationalResponse)
- (instancetype)initWithRespone:(NRConversationalResponse *)response agentType:(AgentType)agentType;
@end

