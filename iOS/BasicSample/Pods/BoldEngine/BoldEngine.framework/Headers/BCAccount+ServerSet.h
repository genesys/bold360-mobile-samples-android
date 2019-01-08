
// NanorepUI version number: v2.3.6.rc2 

//
//  BCAccount+ServerSet.h
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import "BCAccount.h"

/**
 * @brief Private header for setting the server type. Only works with builds that have selector type.
 * @since Version 1.0
 */
@interface BCAccount (ServerSet)

/**
 * @brief Setting the server type to connect to. 
 * @details It is used for development. In public production releases the value does not take effect.
 * @since Version 1.0
 */

@property(nonatomic, strong)NSString *serverSet;

@end
