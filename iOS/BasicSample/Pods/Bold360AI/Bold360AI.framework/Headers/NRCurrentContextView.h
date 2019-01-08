
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>

@protocol NRCurrentContextViewDelegate <NSObject>

- (void)clearContext:(NSString *)context;

@end

@interface NRCurrentContextView : UIView
@property (nonatomic, copy) NSDictionary *context;
@property (nonatomic, copy) NSString *concatedContext;
@property (nonatomic, weak) IBOutlet id<NRCurrentContextViewDelegate> delegate;
- (IBAction)clearContext:(UIButton *)sender;
@end
