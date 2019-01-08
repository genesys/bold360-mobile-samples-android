
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import "ChatElementConfiguration.h"
#import "QuickOptionConfiguration.h"
#import "PersistentOptionConfiguration.h"

/************************************************************/
// MARK: - IncomingBotConfiguration
/************************************************************/

@interface IncomingBotConfiguration : ChatElementConfiguration
@property (strong, nonatomic, readonly) QuickOptionConfiguration *quickOptionConfig;

@property (strong, nonatomic, readonly) PersistentOptionConfiguration *persistentOptionConfig;
@end
