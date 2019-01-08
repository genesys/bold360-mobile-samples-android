
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import "ChatElementConfiguration.h"

/************************************************************/
// MARK: - OutgoingConfiguration
/************************************************************/

@interface OutgoingConfiguration : ChatElementConfiguration
@property (nonatomic, strong) UIImage *sentSuccessfullyIcon;
@property (nonatomic, strong) UIImage *sentFailureIcon;
@property (nonatomic, strong) UIImage *pendingIcon;
@end
