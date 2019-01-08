
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import "BoldTableViewCell.h"

@interface TextTableViewCell : UITableViewCell <BoldTableViewCell, UITextFieldDelegate>
@property (weak, nonatomic) IBOutlet UITextField *textView;


@end
