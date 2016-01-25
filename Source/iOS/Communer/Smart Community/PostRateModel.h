//
//  PostRateModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"

@interface PostRateModel : KVCBaseObject

@property (nonatomic, strong) NSString* serviceContactID;
@property (nonatomic, strong) NSString* rate;
@property (nonatomic, strong) NSString* descr;

@end
