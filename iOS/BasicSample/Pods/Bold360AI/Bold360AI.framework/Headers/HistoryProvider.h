
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import "StorableChatElement.h"

/************************************************************/
// MARK: - HistoryProvider
/************************************************************/

@protocol HistoryProvider

/************************************************************/
// MARK: - Functions
/************************************************************/


/**
 Fetches StorableChatElement Array

 @param from The index of chat item (e.g 0), paging supported
 @param handler Fetch callback that gets StorableChatItem array
 */
- (void)fetch:(NSInteger)from handler:(void(^)(NSArray<StorableChatElement> *elements))handler;

/**
 Store Chat Element

 @param item Represents StorableChatItem
 */
- (void)store:(id<StorableChatElement>)item;

/**
 Remove Chat Element

 @param timestampId Identifier that used to remove StorableChatItem
 */
- (void)remove:(NSTimeInterval)timestampId;

/**
 Update Chat Element

 @param timestampId The timestamp of chat element
 @param newTimestamp New timestamp for updated chat element
 @param status The status of chat element
 */
- (void)update:(NSTimeInterval)timestampId newTimestamp:(NSTimeInterval)newTimestamp status:(StatementStatus)status;

@end
