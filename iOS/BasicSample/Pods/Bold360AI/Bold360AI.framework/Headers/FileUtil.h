
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

@interface FileUtil : NSObject
@property (nonatomic,assign)BOOL debug;
- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)shared;
- (void)write:(NSData *)data forKey:(NSString *)key;
-(NSData *)readForKey:(NSString *)key;
- (void)clearCacheFolder;
@end
