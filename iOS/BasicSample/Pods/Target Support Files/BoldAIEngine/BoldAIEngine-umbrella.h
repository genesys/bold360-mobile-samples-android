#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "Account.h"
#import "AccountParams.h"
#import "BoldAIEngine.h"
#import "FAQAnswer.h"
#import "NanoRep.h"
#import "NRAutoCompleteResponse.h"
#import "NRBaseResponse.h"
#import "NRCacheManager.h"
#import "NRChanneling.h"
#import "NRConfiguration.h"
#import "NRConnection.h"
#import "NRConnectionHandler.h"
#import "NRConstants.h"
#import "NRConversationalResponse.h"
#import "NRConversationCarousel.h"
#import "NRConversationMissingEntity.h"
#import "NRCreateConversationResponse.h"
#import "NRDefaultConnectionHandler.h"
#import "NREntity.h"
#import "NRErrorHandler.h"
#import "NRFAQGroup.h"
#import "NRLabel.h"
#import "NRLikeStateHandler.h"
#import "NRPersonalInfo.h"
#import "NRQueryResult.h"
#import "NRSearchResponse.h"
#import "NRSessionResponse.h"
#import "NRTokenizer.h"
#import "NSArray+Utilities.h"
#import "NSBundle+EngineBundle.h"
#import "NSData+Utilities.h"
#import "NSDictionary+Parsed.h"
#import "NSMutableDictionary+Params.h"
#import "NSString+Resources.h"
#import "NSString+Utilities.h"
#import "NSURLRequest+Channeling.h"
#import "SearchAnswer.h"
#import "SearchContextValue.h"
#import "UIFont+Utilities.h"

FOUNDATION_EXPORT double BoldAIEngineVersionNumber;
FOUNDATION_EXPORT const unsigned char BoldAIEngineVersionString[];

