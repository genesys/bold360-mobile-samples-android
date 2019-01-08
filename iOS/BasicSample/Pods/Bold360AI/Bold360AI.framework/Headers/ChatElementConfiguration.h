
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "ChatViewConfiguration.h"

/************************************************************/
// MARK: - ChatElementConfiguration
/************************************************************/

@interface ChatElementConfiguration : ChatViewConfiguration

/**
 Chat Element Avatar
 */
@property (strong, nonatomic) UIImage *avatar;

/**
 Chat Element Text Size
 */
@property (copy, nonatomic) NSNumber *textSize;

@property (copy, nonatomic) UIColor *textColor;

@end
