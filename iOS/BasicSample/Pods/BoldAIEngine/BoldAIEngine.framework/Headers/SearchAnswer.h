
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import "NRQueryResult.h"
#import "SearchContextValue.h"


@protocol ContextValueHandler <NSObject>

- (SearchContextValue *)contextForId:(NSNumber *)contextId;
@property (nonatomic, readonly) NSString *multipleContext;

@end

@interface SearchAnswer : NRQueryResult
@property (nonatomic, readonly, copy) NSArray<NSNumber *> *contextSelection;
@property (nonatomic, readonly, copy) SearchContextValue *searchContext;
@property (nonatomic, readonly, copy) NSString *contextValue;
@property (nonatomic, strong) id<ContextValueHandler> contextValueHandler;
@end
