
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, NRQuickOptionKind) {
    NRQuickOptionKindCarousel,
    NRQuickOptionKindReadMore,
    NRQuickOptionKindInlineChoice,
    NRQuickOptionKindChannel,
    NRQuickOptionKindWasHelpful,
    NRQuickOptionKindLocation,
    NRQuickOptionKindFeedback,
    NRQuickOptionKindOther,
    NRQuickOptionKindTrack,
    NRQuickOptionKindUNKNOWN
};

typedef NS_ENUM(NSInteger, NRQuickOptionType) {
    NRQuickOptionTypeLocation,
    NRQuickOptionTypeApplication,
    NRQuickOptionTypePostback,
    NRQuickOptionTypeURL,
    NRQuickOptionTypeUNKNOWN
};


@interface NRQuickOption : NSObject
- (void)updateKind:(NRQuickOptionKind)kind;
- (void)updateType:(NRQuickOptionType)type;

- (instancetype)initWithParams:(NSDictionary *)params;

@property(nonatomic, copy) NSString *text;
@property (nonatomic, readonly) NSString *kind;
@property(nonatomic, copy) NSString *postback;
@property(nonatomic, copy) NSString *payload;
@property(nonatomic, readonly) NSString *type;
@property (nonatomic, readonly) NSDictionary *params;
@end
