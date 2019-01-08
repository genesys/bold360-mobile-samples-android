
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// NanorepUI SDK.
// All rights reserved.
// ===================================================================================================

#import "BoldHandler.h"

@interface BoldHandler (BCSubmitPostChatDelegate)<BCSubmitPostChatDelegate>
- (void)handleChatFinished:(BCChatEndReason)reason;
- (void)updateChatState:(ChatState)state type:(AgentType)type;
@end
