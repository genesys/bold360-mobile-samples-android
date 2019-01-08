
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

/************************************************************/
// MARK: - ChatElementType
/************************************************************/

/**
 An AgentType is an enum of different agent states
 */

#import "ChatElementConfiguration.h"

typedef NS_ENUM(NSInteger, ChatElementType) {
    /// Chat element type is local
    OutgoingElement,
    /// Chat element type is remote
    IncomingBotElement,
    /// Chat element type is remote
    IncomingLiveElement,
    /// Chat element type is carousel
    CarouselElement,
    SystemMessageElement
};

typedef NS_ENUM(NSInteger, ChatElementSource) {
    ChatElementSourceDynamic,
    ChatElementSourceHistory
};

/************************************************************/
// MARK: - ChatElement
/************************************************************/

@protocol ChatElement <NSObject>

/************************************************************/
// MARK: - Properties
/************************************************************/

/**
 The DB id
 */
@property (nonatomic, copy, readonly) NSNumber *elementId;

/**
 The type of chat element
 */
@property (nonatomic, readonly) ChatElementType type;
/**
 The creation date of chat item.
 */
@property (nonatomic, copy) NSDate *timestamp;
/**
 The content of chat item (text, json..)
 */
@property (nonatomic, copy, readonly) NSString *text;

@property (nonatomic, assign) ChatElementSource source;

@property (nonatomic, strong) ChatElementConfiguration *configuration;
@end
