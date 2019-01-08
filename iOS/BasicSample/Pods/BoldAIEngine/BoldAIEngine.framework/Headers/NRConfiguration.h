
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "NRLabel.h"
#import "NRBaseResponse.h"

@interface NRComponentConfiguration : NSObject
- (instancetype)initWithParams:(NSDictionary *)params;
@property (nonatomic, strong) UIColor *backgroundColor;
@property (nonatomic, strong) UIFont *font;
@property (nonatomic, strong) UIColor *textColor;
@property (nonatomic, copy) NSDictionary *params;

@end

@interface NRNavBarConfiguration : NRComponentConfiguration
@property (nonatomic, copy) NSString *title;
@end

@interface NRTitleConfiguration : NRComponentConfiguration
@property (nonatomic) CGFloat rowHeight;
@property (nonatomic, copy) NSString *accessoryImageName;
@property (nonatomic) UIEdgeInsets resultInset;
@property (nonatomic) UIEdgeInsets textInset;
@end

@interface NRSearchBarConfiguration : NRComponentConfiguration
@property (nonatomic, copy) NSString *placeHolder;
@property (nonatomic) BOOL voiceEnabled;
@property (nonatomic) UISearchBarStyle searchBarStyle;
@end

@interface NRAutoCompleteConfiguration : NRComponentConfiguration
@property (nonatomic) BOOL autocompleteEnabled;
@property (nonatomic) CGFloat rowHeight;
@property (nonatomic) BOOL withDivider;
@property (nonatomic) UIFont *boldedFont;
@end

@interface NRContentConfiguration : NRComponentConfiguration

@end

typedef NS_ENUM(NSInteger, FeedbackType) {
    FeedbackTypeIcons,
    FeedbackTypeText
};

typedef NS_ENUM(NSInteger, NegativeFeedbackType) {
    NegativeFeedbackTypeBoth,
    NegativeFeedbackTypeMultipleChoices,
    NegativeFeedbackTypeFreeText
};

@interface NRLikeConfiguration : NRComponentConfiguration
@property (nonatomic) BOOL shouldPresentDislikeOptions;
@property (nonatomic) FeedbackType feedbackType;
@property (nonatomic, copy) NSString *feedbackPositiveButtonText;
@property (nonatomic, copy) NSString *feedbackNegativeButtonText;
@property (nonatomic, copy) NSString *feedbackCustomiseText;
@end

@interface NRNegativeFeedbackConfiguration: NRComponentConfiguration
@property (nonatomic, copy) NSString *inAccurateContent;
@property (nonatomic, copy) NSString *notRelevantContent;
@property (nonatomic) NegativeFeedbackType type;
@property (nonatomic, copy) NSString *placeholder;
@property (nonatomic, copy) NSString *submitButtonText;
@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *disclaimer;
@end

@interface NRChannelingConfiguration : NRComponentConfiguration

@end

@interface NRLogoConfiguration : NRComponentConfiguration
@property (nonatomic) BOOL hideBranding;
@end

@interface ConversationSettings: NRBaseResponse
@property (nonatomic, assign) BOOL requireAPIKey;
@property (nonatomic, assign) BOOL channelsEnabled;
@property (nonatomic, assign) BOOL feedbackEnabled;
@property (nonatomic, assign) NSTimeInterval feedbackTimeout;
@property (nonatomic, copy) NSString *feedbackText;
@property (nonatomic, assign) BOOL askAgent;
@property (nonatomic, assign) BOOL showSupportCenterLink;
@property (nonatomic, copy) NSString *multiResultsHeader;
@end

@interface NRConfiguration : NRBaseResponse

@property (nonatomic) UIColor *statusBarColor;
@property (nonatomic, readonly) NRNavBarConfiguration *navBar;
@property (nonatomic, readonly) NRTitleConfiguration *title;
@property (nonatomic, readonly) NRSearchBarConfiguration *searchBar;
@property (nonatomic, readonly) NRAutoCompleteConfiguration *autoComplete;
@property (nonatomic, readonly) NRContentConfiguration *content;
@property (nonatomic, readonly) NRLikeConfiguration *like;
@property (nonatomic, readonly) NRNegativeFeedbackConfiguration *negativeFeedback;
@property (nonatomic, readonly) NRChannelingConfiguration *channeling;
@property (nonatomic, readonly) NRLogoConfiguration *logo;
@property (nonatomic, readonly) ConversationSettings *conversationSettings;

@property (nonatomic, readonly) BOOL isContextDependent;
@property (nonatomic, copy) NSArray<NRFAQGroup *> *faqs;
@property (nonatomic, copy, readonly) NSString *languageCode;
@property (nonatomic, copy, readonly) NSString *kbId;
@property (nonatomic, copy, readonly) NSString *customNoAnswersText;
@property (nonatomic, copy, readonly) NSString *customNoAnswersTextContext;
@property (nonatomic, copy, readonly) NSString *feedbackCustomiseText;
@property (nonatomic, copy, readonly) NSArray<NRFAQGroup *> *labels;
@property (nonatomic) BOOL useLabels;
@end
