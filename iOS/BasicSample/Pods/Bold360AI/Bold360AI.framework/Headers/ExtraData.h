
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

@interface Value : NSObject
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *type;
@property (nonatomic, copy) id value;
@end

@interface ExtraData : NSObject
@property (nonatomic, copy) NSString *postUrl;
@property (nonatomic, copy) NSArray<Value *> *values;
@property (nonatomic, copy) NSDictionary<NSString *, NSObject *> *fileInfos;
@end
