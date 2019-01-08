
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BCFormFieldOption;

/** @file */
/**
 * @brief The type of a form field.
 * @since Version 1.0
 */
typedef enum {
    BCFormFieldTypeText, /**< The form should allow the input of arbitrary text. */
    BCFormFieldTypeSelect, /**< The form should show a selection field with a pre-defined set of options. */
    BCFormFieldTypeRadio, /**< The form should show a radio field with a pre-defined set of options. It is also acceptable to treat this the same as the \link BCFormFieldTypeSelect \endlink type. */
    BCFormFieldTypeRating, /**< The form should show a rating selection with possible values 1-5. Fractions are not allowed.*/
    BCFormFieldTypePhone, /**< The form should allow the input of arbitrary text resembling a phone number. This can be treated the same as BCFormFieldTypeText but should restrict the keyboard if possible. */
    BCFormFieldTypeEmail, /**< The form should allow the input of arbitrary text resembling an email address. This can be treated the same as BCFormFieldTypeText but should restrict the keyboard if possible. */
}BCFormFieldType;

/**
 * @brief Container class for information about an individual form field.  This class also contains the value to be submitted.
 * @since Version 1.0
 */
@interface BCFormField : NSObject

/**
 * @brief Type of the form field.
 * @since Version 1.0
 */
@property(nonatomic, readonly)BCFormFieldType type;

/**
 * @brief When submitting this is the key field that the server will be expecting.
 * @since Version 1.0
 */
@property(nonatomic, copy, readonly)NSString *key;

/**
 * @brief If the text field is \link BCFormFieldTypeText \endlink then this field indicates if the text input field should have multiple lines.
 * @since Version 1.0
 */
@property(nonatomic, readonly)BOOL isMultiline;

/**
 * @brief The prompt label for the form field.
 * @since Version 1.0
 */
@property(nonatomic, copy)NSString *label;

/**
 * @brief The branding value key that should be used for the label, or null if this is a custom field.  If this returns nil then label should be used instead.
 * @since Version 1.0
 */
@property(nonatomic, copy, readonly)NSString *labelBrandingKey;

/**
 * @brief Indicates if there should be validation on this field to ensure the user enters information, or selects a value.
 * @since Version 1.0
 */
@property(nonatomic, readonly)BOOL isRequired;

/**
 * @brief Determines if the field should be shown to the user or not.
 * @since Version 1.0
 */
@property(nonatomic, readonly)BOOL isVisible;

/**
 * @brief If the field is type \link BCFormFieldTypeSelect \endlink and is a BoldChat department selection type of field then this field indicates if the online/offline status of the department should be shown to the user.
 * @since Version 1.0
 */
@property(nonatomic, readonly)BOOL isDepartmentStatusVisible;

/**
 * @brief If the form field type is \link BCFormFieldTypeSelect \endlink or \link BCFormFieldTypeRadio \endlink it holds the default option.
 * @since Version 1.0
 */
@property(nonatomic, readonly)BCFormFieldOption *defaultOption;

/**
 * @brief If the field is \link BCFormFieldTypeSelect \endlink or \link BCFormFieldTypeRadio \endlink then this method returns all the options that should be given to the user. The array holds \link BCFormFieldOption \endlink objects.
 * @since Version 1.0
 */
@property(nonatomic, copy, readonly)NSArray *options;

/**
 * @brief The value that has been set for this field, or nil if is unset.
 * @since Version 1.0
 */
@property(nonatomic, strong)NSString *value;

/**
 * @brief The static constructor for BCFormField.
 * @param type Type of the form field.
 * @param key When submitting this is the key field that the server will be expecting.
 * @param isMultiline If the text field is BCFormFieldTypeText then this field indicates if the text input field should have multiple lines.
 * @param label The title label text of the field.
 * @param labelBrandingKey The branding value key that should be used for the label, or null if this is a custom field.  If this returns nil then label should be used instead.
 * @param isRequired It indicates if there should be validation on this field to ensure the user enters information, or selects a value.
 * @param isVisible It determines if the field should be shown to the user or not.
 * @param isDepartmentStatusVisible If the field is type \link BCFormFieldTypeSelect \endlink and is a BoldChat department selection type of field then this field indicates if the online/offline status of the department should be shown to the user.
 * @param defaultValue The default value of the form field, if the type is not \link BCFormFieldTypeSelect \endlink or \link BCFormFieldTypeRadio \endlink.
 * @param options If the field is \link BCFormFieldTypeSelect \endlink or \link BCFormFieldTypeRadio \endlink then this method returns all the options that should be given to the user.
 * @returns An instance of BCFormField class.
 * @since Version 1.0
 */
+ (id)formFieldWithType:(BCFormFieldType)type
                    key:(NSString *)key
            isMultiline:(BOOL)isMultiline
                  label:(NSString *)label
       labelBrandingKey:(NSString *)labelBrandingKey
             isRequired:(BOOL)isRequired
              isVisible:(BOOL)isVisible
isDepartmentStatusVisible:(BOOL)isDepartmentStatusVisible
           defaultValue:(NSString *)defaultValue
                options:(NSArray *)options;

/**
 * @brief The constructor for BCFormField.
 * @param type Type of the form field.
 * @param key When submitting this is the key field that the server will be expecting.
 * @param isMultiline If the text field is \link BCFormFieldTypeText \endlink then this field indicates if the text input field should have multiple lines.
 * @param label The title label text of the field.
 * @param labelBrandingKey The branding value key that should be used for the label, or null if this is a custom field. If this returns nil then label should be used instead.
 * @param isRequired It indicates if there should be validation on this field to ensure the user enters information, or selects a value.
 * @param isVisible It determines if the field should be shown to the user or not.
 * @param isDepartmentStatusVisible If the field is type \link BCFormFieldTypeSelect \endlink and is a BoldChat department selection type of field then this field indicates if the online/offline status of the department should be shown to the user.
 * @param defaultValue The default value of the form field, if the type is not \link BCFormFieldTypeSelect \endlink or \link BCFormFieldTypeRadio \endlink.
 * @param options If the field is \link BCFormFieldTypeSelect \endlink or \link BCFormFieldTypeRadio \endlink then this method returns all the options that should be given to the user.
 * @since Version 1.0
 */
- (id)initWithType:(BCFormFieldType)type
               key:(NSString *)key
       isMultiline:(BOOL)isMultiline
             label:(NSString *)label
  labelBrandingKey:(NSString *)labelBrandingKey
        isRequired:(BOOL)isRequired
         isVisible:(BOOL)isVisible
isDepartmentStatusVisible:(BOOL)isDepartmentStatusVisible
      defaultValue:(NSString *)defaultValue
           options:(NSArray *)options;

@end
