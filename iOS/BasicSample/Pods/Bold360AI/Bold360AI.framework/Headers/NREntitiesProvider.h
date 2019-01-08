
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import <BoldAIEngine/BoldAIEngine.h>

@protocol NREntitiesProvider <NSObject>
- (void)shouldHandleMissingEntities:(NRConversationalResponse *)response
             missingEntitiesHandler:(void(^)(NRConversationMissingEntity *missingEntity))handler;

- (void)shouldHandlePersonalInformation:(NRPersonalInfo *)personalInfo;

@property (nonatomic, copy) NSArray<NSString *> *entities;
@end
