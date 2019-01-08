
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "StorableChatElement.h"
#import "ChatEventHandler.h"
#import "ChatControllerDelegate.h"
#import "PreChatInfo.h"
#import "ContinuityProvider.h"

typedef NS_ENUM(NSInteger, ChatEvent) {
    ChatEventReadmore,
    ChatEventChannel
};

@protocol ChatHandler;

@protocol ChatHandlerDelegate <NSObject>

- (void)presentStatement:(id<StorableChatElement>)statement;

/**
 Update chat status (chat lifecycle)
 
 @param status chat status
 @param element chat element
 */
- (void)updateStatus:(StatementStatus)status element:(id<StorableChatElement>)element;

/**
 Event handler
 
 @param event event name
 @param params any params
 */
- (void)event:(ChatEvent)event withParams:(NSDictionary *)params;

- (void)preChat:(PreChatInfo *)preChatInfo;
- (void)postChat:(NSDictionary *)postChatInfo;
@end

@protocol ChatHandlerProvider <NSObject>
- (void)shouldReplaceChatHandelr:(NSDictionary *)chatHandlerParams;
- (void)didEndChat:(id<ChatHandler>)chatHandler;
- (void)presentForm:(BrandedForm *)form;
- (ChatElementConfiguration *)configurationForType:(ChatElementType)type;
- (void)didStartChat;
- (void)didFailStartChatWithError:(NSError *)error;
@end

@protocol ChatHandler <ChatEventHandler>
- (void)startChat:(NSDictionary *)chatInfo;
- (void)endChat;
- (void)postStatement:(id<StorableChatElement>)statement;
- (void)postArticle:(NSString *)articleId;
- (void)submitForm:(BrandedForm *)form;

@property (nonatomic, weak) id<ChatHandlerDelegate> delegate;
@property (nonatomic, weak) id<ChatControllerDelegate> chatControllerDelegate;
@property (nonatomic, weak) id<ChatHandlerProvider> chatHandlerProvider;
@optional
@property (nonatomic, weak) id<ContinuityProvider> continuityProvider;
@end


