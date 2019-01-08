
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "SearchAnswer.h"




@interface SearchContext : NRBaseResponse
@property (nonatomic) NSInteger v;
@property (nonatomic, copy, readonly) NSArray<SearchContextValue *> *values;
@property (nonatomic, copy, readonly) NSArray<NSString *> *order;
@property (nonatomic, copy) NSString *multipleContext;
@end


@interface NRSearchResponse : NRBaseResponse <ContextValueHandler>

//- (instancetype)initWithParams:(NSDictionary *)params;

/**
 *  Specific search Id
 */
@property (nonatomic, readonly) NSString *requestId;

/**
 *  Language code for the current search.
 */
@property (nonatomic, readonly) NSString *kbLanguageCode;


/**
 *  Languages that can be used for the current search.
 */
@property (nonatomic, readonly) NSString *detectedLanguage;

/**
 *  Array of NRAnswer objects
 */
@property (nonatomic, readonly) NSArray<SearchAnswer *> *answerList;

@property (nonatomic, readonly) SearchContext *context;

//@property (nonatomic, readonly, getter = isDynamicContext) BOOL dynamicContext;

@property (nonatomic, readonly) NSArray<NRChanneling *> *channels;
@end
