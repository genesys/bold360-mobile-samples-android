
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import "NRConnectionHandler.h"

extern NSString *const NRStatusKey;


@interface NRResponse<ObjectType> : NSObject
@property (nonatomic, readonly) NSError *error;
@property (nonatomic, readonly) ObjectType type;
@property (nonatomic, readonly, copy) NSData *cacheParams;
@property (nonatomic, readonly) NSInteger status;

@property (nonatomic, weak) id<NRConnectionHandler> connectionHandler;
@end


@interface NRConnection : NSObject


//+ (void)connection:(NSURLComponents *)urlComponents completion:(void(^)(NRResponse *reponse))completion;

+ (void)openConnection:(id<NRConnectionHandler>)connectionHandler
            components:(NSURLComponents *)urlComponents
            completion:(void(^)(NRResponse *reponse))completion;

@end
