
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// NanorepUI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>

/************************************************************/
// MARK: - PreChatBrand
/************************************************************/

@interface PreChatBrand : NSObject
- (instancetype)initWithBranding:(NSDictionary *)branding;

@property (nonatomic, copy, readonly) NSString *introMessage;
@property (nonatomic, copy, readonly) NSString *startChat;
@property (nonatomic, copy, readonly) NSString *required;
@property (nonatomic, copy, readonly) NSString *departmentAvailable;
@property (nonatomic, copy, readonly) NSString *departmentUnavailable;
@property (nonatomic, copy, readonly) NSString *invalidEmail;
@property (nonatomic, copy, readonly) NSString *invalidPhoneNumber;
@property (nonatomic, copy, readonly) NSString *invalidInput;

@end
