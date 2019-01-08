
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import "BLDCacheModel.h"

extern NSString *const URLMIMETYPE;


typedef NS_ENUM(NSInteger,CacheType) {
    CacheTypeImage = 4,
    CacheTypeJS,
    CacheTypeCSS,
    CacheTypeHTML
};
@interface BLDWKWebViewCache : NSObject


/**
 Initialize the web cache. This method can be called directly in application:didFinishLaunchingWithOptions
 
 @param arr cache type, CacheType
 */
- (void)initWebCacheWithCacheTypes:(NSArray *)arr;

+(instancetype)sharedWebViewCache;


- (instancetype)init NS_UNAVAILABLE;

/**
 Register cache
 */
+ (void)registerSchemeCache;

/**
 Do not register cache
 */
+ (void)unRegisterSchemeCache;

/**
 Get cached data

 @param cacheKey cache key
 @return returns the cache model
 */
- (BLDCacheModel *)getCacheDataByKey:(NSString *)cacheKey;

/**
 Set the offline cache, according to the demand cache,
 if your js has a version number, then if there is an update,
 the cache key will be inconsistent with the original key,
 to achieve the update effect. Instant updates are not possible if the same link content changes.

 @param key cache key
 @param model cache model
 */
- (void)setCacheWithKey:(NSString *)key value:(BLDCacheModel *)model;


/**
 Clear the cache and call it in the right place, such as clearing the cache button click
 */
- (void)clearZGCache;

- (NSArray *)cacheArr;
/**
 clear cache

 @param day days, for example 7 days, will automatically clear all caches after 7 days, this method can be called directly in application:didFinishLaunchingWithOptions
 */
- (void)clearCacheWithInvalidDays:(NSInteger)day;


/**
 Set debug mode, whether to print the log, the default is NO, please set it to NO on the line.

 @param boo YES means to enable debug mode, NO means to turn off debugM mode
 */
- (void)setDebugModel:(BOOL)boo;

@end
