
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import "IncomingBotConfiguration.h"
#import "IncomingLiveConfiguration.h"
#import "OutgoingConfiguration.h"
#import "CarouselConfiguration.h"
#import "SystemMessageConfiguration.h"


/************************************************************/
// MARK: - ChatConfiguration
/************************************************************/

@interface ChatConfiguration : NSObject

/**
 Chat View Configuration
 */
@property (strong, nonatomic, readonly) ChatViewConfiguration *chatViewConfig;

/**
 Incoming Bot Configuration
 */
@property (strong, nonatomic, readonly) IncomingBotConfiguration *incomingBotConfig;

/**
 Incoming Live Configuration
 */
@property (strong, nonatomic, readonly) IncomingLiveConfiguration *incomingLiveConfig;

/**
 Outgoing Configuration
 */
@property (strong, nonatomic, readonly) OutgoingConfiguration *outgoingConfig;

@property (strong, nonatomic, readonly) CarouselConfiguration *carouselConfig;

@property (strong, nonatomic, readonly) SystemMessageConfiguration *systemMessageConfig;


@end
