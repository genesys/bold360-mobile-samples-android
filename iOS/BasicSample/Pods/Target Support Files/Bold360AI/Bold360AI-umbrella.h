#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "BoldFormSelection.h"
#import "BoldHandler.h"
#import "BrandedForm.h"
#import "BoldHandler+BCChatMessageDelegate.h"
#import "BoldHandler+BCChatRecaptureDelegate.h"
#import "BoldHandler+BCCreateChatSessionDelegate.h"
#import "BoldHandler+BCSubmitPreChatDelegate.h"
#import "BoldHandler+ChatStateDelegate.h"
#import "PreChatBrand.h"
#import "DefaultBoldFormSelection.h"
#import "NSBundle+UIBoldBundle.h"
#import "BoldForm.h"
#import "BoldCellInjector.h"
#import "CellInjectorFactory.h"
#import "SelectCellInjector.h"
#import "EmailTextInjector.h"
#import "PhoneTextInjector.h"
#import "TextCellInjector.h"
#import "PreChatTableViewController.h"
#import "BoldCellData.h"
#import "BoldTableViewCell.h"
#import "SelectTableViewCell.h"
#import "TextTableViewCell.h"

FOUNDATION_EXPORT double Bold360AIVersionNumber;
FOUNDATION_EXPORT const unsigned char Bold360AIVersionString[];

