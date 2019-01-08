
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>

/************************************************************/
// MARK: - IncomingLiveConfiguration
/************************************************************/

@interface ChatViewConfiguration : NSObject

/**
 Chat View Background Color
 */
@property (strong, nonatomic) UIColor *backgroundColor;

/**
 Chat View Background Image
 */
@property (strong, nonatomic) UIImage *backgroundImage;

@property (copy, nonatomic) UIColor *dateStampColor;

@end
