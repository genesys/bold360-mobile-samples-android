
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

@interface BLDCacheModel : NSObject

/**
 mime type
 */
@property (nonatomic,strong)NSString *MIMEType;


/**
 Cache data
 */
@property (nonatomic,strong)NSData *data;
@end
