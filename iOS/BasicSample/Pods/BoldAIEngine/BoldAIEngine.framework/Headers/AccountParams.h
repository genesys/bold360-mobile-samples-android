
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <BoldCore/Account.h>
@interface DynamicContext: NSObject
- (instancetype)initWithContext:(NSString *)context;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *context;
@end

@interface AccountParams : Account
@property (nonatomic, copy) NSString *account;
@property (nonatomic, copy) NSString *knowledgeBase;
@property (nonatomic, copy) NSDictionary<NSString *, NSString *> *nanorepContext;
@property (nonatomic, copy) NSString *referrer;
@property (nonatomic, readonly, copy) NSString *accountId;
@property (nonatomic, copy) NSString *labelId;
@property (nonatomic, copy, readonly) NSString *dynamicContextDescription;
@property (nonatomic) BOOL allContextsMandatory;

- (void)appendDynamicContext:(DynamicContext *)context;

- (NSDictionary<NSString *,NSString *> *)resetDynamicContext;
@end
