
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "NRConfiguration.h"
#import "AccountParams.h"
#import "NRErrorHandler.h"
#import "NRSearchResponse.h"
#import "NRAutoCompleteResponse.h"
#import "NRConversationalResponse.h"
#import "NRLikeStateHandler.h"
#import "NRConnectionHandler.h"



extern NSString *const NRAlertMessageKey;
extern NSString *const NRAlertOKButtonKey;
extern NSString *const NRAlertCancelButtonKey;

@protocol NanoRepDelegate <NSObject>

@optional
- (void)accountReady:(AccountParams *)account;

@end


@protocol NRChatEngineDelegate <NSObject>


- (void)didFetchConvesationId:(NSNumber *)conversationId;

- (void)shouldHandleMissingEntities:(NRConversationalResponse *)response
             missingEntitiesHandler:(void(^)(NRConversationMissingEntity *missingEntity))handler;

- (void)shouldHandlePersonalInformation:(NRPersonalInfo *)personalInfo;

@end



/**
 This is the NanoRep core class providing NanoRep API requests.
 */
@interface NanoRep : NSObject


+ (NanoRep *)shared;

- (void)prepareWithAccountParams:(AccountParams *)accountParams;

@property (nonatomic, copy) void (^fetchConfiguration)(NRConfiguration *configuration, NSError *error);

@property (nonatomic, readonly) AccountParams *accountParams;

@property (nonatomic, strong, readonly) NRConfiguration *configuration;

@property (nonatomic, strong, readonly) NRErrorHandler *errorHandler;

@property (nonatomic, weak) id<NRChatEngineDelegate> delegate;

@property (nonatomic, readonly) BOOL isPrepared;

@property (nonatomic, readonly) NRLikeStateHandler *likeState;

@property (nonatomic, weak) id<NRChatEngineDelegate> chatDelegate;

@property (nonatomic, strong) id<NRConnectionHandler> connectionHandler;

- (void)fetchFAQAnswer:(NSString *)answerId
                  hash:(NSNumber *)hash
            completion:(void (^) (NRQueryResult *result, NSError *error))completion;


// Depends on cnf
- (void)createConversationWithEntities:(NSArray<NSString *> *)entities
                              textOnly:(BOOL)textOnly
                            completion:(void(^)(NSDictionary *cnversationParams, NSError *error))completion;

- (void)conversationWithEntities:(NSArray<NSString *> *)entities
                        textOnly:(BOOL)textOnly
                      completion:(void(^)(NSDictionary *cnversationParams, NSError *error))completion;

- (void)conversationStatement:(NSString *)statement
                         type:(BOOL)isPostback
               conversationId:(NSNumber *)conversationId
                   completion:(void(^)(NRConversationalResponse *response, NSError *error))completion;

- (void)conversationArticle:(NSString *)articleId
             conversationId:(NSNumber *)conversationId
                 completion:(void (^)(NRConversationalResponse *, NSError *))completion;

//- (void)sendMissingEntities:(NSString *)missingEntities
//             conversationId:(NSString *)conversationId
//                  statement:(NSString *)statement
//                 completion:(void (^)(NRConversationalResponse *response, NSError *error))completion;


- (void)faqForLabel:(NRLabel *)label
         completion:(void(^)(NSArray<NRQueryResult *> *results))completion;

- (void)searchText:(NSString *)text
        completion:(void(^)(NRSearchResponse *searchResponse, NSError *error))completion;

- (void)contextValue:(NSString *)contexts
          completion:(void(^)(NSDictionary *values, NSError *error))completion;

- (void)changeContext:(NSDictionary<NSString *, NSString *> *)context
           completion:(void(^)(BOOL success, NSError *error))completion;

- (void)suggestionsForText:(NSString *)text
                completion:(void(^)(NRAutoCompleteResponse *autoCompleteResponse, NSError *error))completion;

- (void)channels:(NRQueryResult *)result
         context:(NSString *)context
      completion:(void(^)(NSArray<NRChanneling *> *channels, NSError *error))completion;

- (void)like:(int)likeType
   forResult:(NRQueryResult *)result
  completion:(void(^)(NSString *resultId, int type, BOOL success))completion;

- (void)trackEvent:(NSDictionary *)eventParams;

- (void)sendConversationFeedbackWithCompletion:(void(^)(NRConversationalResponse *respons))completion;

- (void)reportChanneling:(NRChanneling *)channel article:(NRQueryResult *)result;

- (void)stop;

@end
