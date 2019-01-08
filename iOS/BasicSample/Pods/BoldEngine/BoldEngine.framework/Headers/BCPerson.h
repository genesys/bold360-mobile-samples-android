
// NanorepUI version number: v2.3.6.rc2 

//
//  Copyright (c) 2014 LogMeIn Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

/** @file */
/**
 * @brief Defines the different types of people that may type or send messages.
 * @since Version 1.0
 */
typedef enum {
    BCPersonTypeUndefined, /**< The type is not defined */
    BCPersonTypeVisitor, /**< The visitor that is chatting (user of this api). */
    BCPersonTypeOperator, /**< The operator handling the chat. */
    BCPersonTypeSystem, /**< An automated message of the operational system. */
}BCPersonType;


/**
 * @brief An entity participating in the chat. It can be either a visitor, an operator or the system.
 * @since Version 1.0
 */
@interface BCPerson : NSObject

/**
 * @brief The id of the person.
 * @since Version 1.0
 */
@property(nonatomic, copy)NSString *personId;

/**
 * @brief The type of the person.
 * @since Version 1.0
 */
@property(nonatomic, assign)BCPersonType personType;

/**
 * @brief The name of the person to be displayed.
 * @since Version 1.0
 */
@property(nonatomic, copy)NSString *name;

/**
 * @brief The image URL of the person.
 * @since Version 1.0
 */
@property(nonatomic, copy)NSString *imageUrl;

@end
