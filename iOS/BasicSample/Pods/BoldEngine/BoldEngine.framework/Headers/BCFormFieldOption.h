
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * @brief Container class for storing information about an option for \link BCFormFieldTypeSelect \endlink or \link BCFormFieldTypeRadio \endlink.
 * @since Version 1.0
 */
@interface BCFormFieldOption : NSObject

/**
 * @brief The name of the field, this is appropriate to show to a user.
 * @since Version 1.0
 */
@property(nonatomic, copy, readonly)NSString *name;

/**
 * @brief The value of the field, this is not appropriate to show to a user.
 * @since Version 1.0
 */
@property(nonatomic, copy, readonly)NSString *value;

/**
 * @brief The branding key for name localization if exists for the option.
 * @since Version 1.0
 */
@property(nonatomic, copy, readonly)NSString *nameBrandingKey;

/**
 * @brief For deparments selection fields that have online/offlinet status, this will return the status label to be displayed to the user.
 * @since Version 1.0
 */
@property(nonatomic, copy)NSString *availableLabel;

/**
 * @brief YES if the value is the default.
 * @since Version 1.0
 */
@property(nonatomic, readonly)BOOL isDefaultValue;

/**
 * @brief YES if the value is the default one for the \link BCFormFieldTypeSelect \endlink or \link BCFormFieldTypeRadio \endlink field.
 * @since Version 1.0
 */
@property(nonatomic, readonly)BOOL isAvailable;

/**
 * @brief Determines if \link BCFormFieldOption::isAvailable isAvailable \endlink and \link BCFormFieldOption::availableLabel availableLabel \endlink values are set.
 * @since Version 1.0
 */
@property(nonatomic, readonly)BOOL isAvailiblitySet;

/**
 * @brief The static constructor for items options that does not have availability settings.
 * @param name The name of the field, this is appropriate to show to a user.
 * @param value The value of the field, this is not appropriate to show to a user.
 * @param nameBrandingKey The branding key for name localization if exists for the option.
 * @param isDefaultValue YES if the value is the default.
 * @returns An instance of BCFormFieldOption.
 * @since Version 1.0
 */
+ (id)formOptionWithName:(NSString *)name value:(NSString *)value nameBrandingKey:(NSString *)nameBrandingKey isDefaultValue:(BOOL)isDefaultValue;

/**
 * @brief The static constructor for items options that have availability settings.
 * @param name The name of the field, this is appropriate to show to a user.
 * @param value The value of the field, this is not appropriate to show to a user.
 * @param nameBrandingKey The branding key for name localization if exists for the option.
 * @param isDefaultValue YES if the value is the default.
 * @param isAvailable YES if the value is the default one for the \link BCFormFieldTypeSelect \endlink or \link BCFormFieldTypeRadio \endlink field.
 * @param availableLabel For deparments selection fields that have online/offlinet status, this will return the status label to be displayed to the user.
 * @returns An instance of BCFormFieldOption.
 * @since Version 1.0
 */
+ (id)formOptionWithName:(NSString *)name value:(NSString *)value nameBrandingKey:(NSString *)nameBrandingKey isDefaultValue:(BOOL)isDefaultValue isAvailable:(BOOL)isAvailable availableLabel:(NSString *)availableLabel;

/**
 * @brief The constructor for items options that does not have availability settings.
 * @param name The name of the field, this is appropriate to show to a user.
 * @param value The value of the field, this is not appropriate to show to a user.
 * @param nameBrandingKey The branding key for name localization if exists for the option.
 * @param isDefaultValue YES if the value is the default.
 * @returns An instance of BCFormFieldOption.
 * @since Version 1.0
 */
- (id)initWithName:(NSString *)name value:(NSString *)value nameBrandingKey:(NSString *)nameBrandingKey isDefaultValue:(BOOL)isDefaultValue;

/**
 * @brief The constructor for items options that have availability settings.
 * @param name The name of the field, this is appropriate to show to a user.
 * @param value The value of the field, this is not appropriate to show to a user.
 * @param nameBrandingKey The branding key for name localization if exists for the option.
 * @param isDefaultValue YES if the value is the default.
 * @param isAvailable YES if the value is the default one for the \link BCFormFieldTypeSelect \endlink or \link BCFormFieldTypeRadio \endlink field.
 * @param availableLabel For deparments selection fields that have online/offlinet status, this will return the status label to be displayed to the user.
 * @returns An instance of BCFormFieldOption.
 * @since Version 1.0
 */
- (id)initWithName:(NSString *)name value:(NSString *)value nameBrandingKey:(NSString *)nameBrandingKey isDefaultValue:(BOOL)isDefaultValue isAvailable:(BOOL)isAvailable availableLabel:(NSString *)availableLabel;
@end
