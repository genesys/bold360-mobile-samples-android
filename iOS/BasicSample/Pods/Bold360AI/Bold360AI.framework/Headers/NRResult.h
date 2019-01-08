
// NanorepUI version number: v3.2.0.rc4 

// ===================================================================================================
// Copyright © 2018 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

#import <UIKit/UIKit.h>
#import <BoldAIEngine/BoldAIEngine.h>
#import "NRDefaultViewAdapter.h"



@interface NRResult : NSObject
- (instancetype)initWithResult:(NRQueryResult *)queryResult;
@property (nonatomic, readonly) NRQueryResult *queryResult;
@property (nonatomic, getter=isSingle) BOOL single;
@property (nonatomic, getter=isUnfolded) BOOL unfolded;
@property (nonatomic) CGFloat height;
@property (nonatomic, copy) NSString *accountName;
@property (nonatomic, copy) NSString *kb;
@property (nonatomic) CGFloat y;
@property (nonatomic) NRDefaultViewAdapter *viewAdapter;
@end
