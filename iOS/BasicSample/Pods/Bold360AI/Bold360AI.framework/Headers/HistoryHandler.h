
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "HistoryProvider.h"
#import "StorableChatElement.h"
#import "HistoryProvider.h"

/************************************************************/
// MARK: - HistoryHandler
/************************************************************/

@interface HistoryHandler : NSObject

/************************************************************/
// MARK: - Functions
/************************************************************/

/**
 load history

 @param accountId conversation id
 @param from index for paging
 @param handler history laoded handler
 */
- (void)load:(NSNumber *)accountId
        from:(NSInteger)from
     handler:(void(^)(NSArray<StorableChatElement> *elements))handler;

/**
 add history element

 @param element chat element
 */
- (void)add:(id<StorableChatElement>)element;

/**
 remove history item

 @param timestamp used as history elemnt id
 */
- (void)remove:(NSTimeInterval)timestamp;

/**
 update history item

 @param timestampId used as history elemnt id
 @param newTimestamp updated history elemnt id
 @param status chat element status
 */
- (void)update:(NSTimeInterval)timestampId
  newTimestamp:(NSTimeInterval)newTimestamp
        status:(StatementStatus)status;

/************************************************************/
// MARK: - Initialization
/************************************************************/

- (instancetype)initWithProvider:(id<HistoryProvider>)provider;

@end
