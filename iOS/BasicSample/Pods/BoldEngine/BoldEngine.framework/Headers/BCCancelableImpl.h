
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BCCancelable.h"

@class BCCancelableImpl;

/**
 * @brief BCCancelableImpl call back delegate on cancel.
 * @since Version 1.0
 */
@protocol BCCancelableImplDelegate <NSObject>

/**
 * @brief Cancel was initiated.
 * @param cancelableImpl The cancelable that the cancel was called on.
 * @since Version 1.0
 */
- (void)bcCancelableImplDidCancel:(BCCancelableImpl *)cancelableImpl;
@end
/**
 * @brief BCCancelable implementation.
 * @since Version 1.0
 */
@interface BCCancelableImpl : NSObject <BCCancelable> {
   @private
    /**
     * @brief Delegate for calling back.
     * @since Version 1.0
     */
    __unsafe_unretained id<BCCancelableImplDelegate> _delegate;
}
/**
 * @brief Constructor.
 * @param delegate The delegate to call back.
 * @since Version 1.0
 */
- (instancetype)initWithDelegate:(id<BCCancelableImplDelegate>)delegate;

/**
 * @brief Clears the delegate to be nil.
 * @since Version 1.0
 */
- (void)clear;


@end
