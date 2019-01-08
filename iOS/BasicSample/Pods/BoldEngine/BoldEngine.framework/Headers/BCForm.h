
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
@class BCFormField;

/** @file */
/**
 * @brief The type of a form.
 * @since Version 3.0
 */
typedef enum {
    BCFormTypePreChat, /**< The form type is PreChat. */
    BCFormTypePostChat, /**< The form type is PostChat. */
    BCFormTypeUnavailable
}BCFormType;

/**
 * @brief Container class for holding the information related to showing a form such as the pre chat, or post chat form to a user.
 * @since Version 1.0
 */
@interface BCForm : NSObject

/**
 * @brief Type of the form.
 * @since Version 3.0
 */
@property(nonatomic, readonly)BCFormType type;

/**
 * @brief An array of BCFormField objects that represent rows of the form.
 * @since Version 1.0
 */
@property(nonatomic, copy, readonly)NSArray *formFields;

/**
 * @brief The static constructor for BCForm.
 * @param formFields An array of BCFormField objects representing rows of the form.
 * @returns An instance of BCForm.
 * @since Version 1.0
 */
+ (id)formWithFormFields:(NSArray *)formFields;

/**
 * @brief The constructor for BCForm.
 * @param formFields An array of BCFormField objects representing rows of the form.
 * @returns An instance of BCForm.
 * @since Version 1.0
 */
- (id)initWithFormFields:(NSArray *)formFields;

/**
 * @brief It returns the form field identified by the given key, if exists.
 * @param key The key of the searched field.
 * @returns The requested form field.
 * @since Version 1.0
 */
- (BCFormField *)formFieldByKey:(NSString *)key;


@end
