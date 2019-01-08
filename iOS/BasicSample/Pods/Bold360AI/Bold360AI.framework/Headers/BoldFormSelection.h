
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <Foundation/Foundation.h>
#import <BoldEngine/BCFormFieldOption.h>

//@protocol BoldFormSelectionDelegate
//- (void)didSelectOption:(BCFormFieldOption *)option;
//@end

@protocol BoldFormSelection
- (UIViewController *)controllerForSelection:(NSArray<BCFormFieldOption *> *)options
                                  completion:(void (^)(BCFormFieldOption *option))selectionHandler;

@end
