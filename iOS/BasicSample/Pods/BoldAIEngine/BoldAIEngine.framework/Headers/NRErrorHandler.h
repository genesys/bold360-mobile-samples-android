
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, NRErrorSource) {
    NRErrorSourceNoConnection,
    NRErrorSourceTimeOut,
    NRErrorSourceServerError
};

@protocol NRErrorHandlerDelegate <NSObject>

- (void)didFailWithSource:(NRErrorSource)errorSource;
- (void)didRecover;

@end

@interface NRErrorHandler : NSObject
+ (NRErrorHandler *)shared;


@property (nonatomic, strong) NSError *error;
@property (nonatomic) BOOL errorState;

@property (nonatomic, weak) id<NRErrorHandlerDelegate> delegate;
@end
