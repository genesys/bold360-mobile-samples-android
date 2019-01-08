
// NanorepUI version number: v1.6.1.rc2 

// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

extern NSString * _Nonnull const EntityTypeNumber;
extern NSString * _Nonnull const EntityTypeText;
extern NSString * _Nonnull const EntityTypeDate;
extern NSString * _Nonnull const EntityTypeDuration;

extern NSString * _Nonnull const EntityLifecyclePersistent;
extern NSString * _Nonnull const EntityLifecycleStatement;
extern NSString * _Nonnull const EntityLifecycleTopic;


@interface Property : NSObject
- (instancetype)initWithKind:(NSString *)kind type:(NSString *)type value:(NSString *)value name:(NSString *)name;

@property (nonatomic, copy) NSString * _Nullable kind;
@property (nonatomic, copy) NSString * _Nullable type;
@property (nonatomic, copy) NSString * _Nullable value;
@property (nonatomic, copy) NSString * _Nullable name;

- (void)addProperty:(Property *_Nullable)property;
@end

@interface NREntity : NSObject
- (instancetype _Nullable)initWithParams:(NSDictionary *_Nullable)params;
@property (nonatomic, copy) NSNumber * _Nullable confidence;
@property (nonatomic, copy) NSNumber * _Nullable statementId;
@property (nonatomic, copy) NSString * _Nullable type;
@property (nonatomic, copy) NSString * _Nullable value;
@property (nonatomic, copy) NSString * _Nullable kind;
@property (nonatomic, copy) NSString * _Nullable lifecycle;
@property (nonatomic) BOOL isPublic;
- (void)addProperty:(Property *_Nullable)property;
@property (nonatomic, copy, readonly) NSDictionary * _Nullable inJSON;
@end
