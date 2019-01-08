
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "NRConversationMissingEntity.h"

@interface NRConversationCarousel : NSObject
@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *subTitle;
@property (nonatomic, copy) NSString *imageUrl;
@property (nonatomic) BOOL isDefault;
@property (nonatomic, copy) NSArray<NRConversationPersistentOption *> *persistentOptions;
@end
