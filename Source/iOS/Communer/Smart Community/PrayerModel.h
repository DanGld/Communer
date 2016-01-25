//
//  PrayerModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"

@interface PrayerModel : KVCBaseObject

@property (nonatomic, strong) NSArray* time;
@property (nonatomic, strong) NSString* name;
@property (nonatomic, strong) NSString* type;
@property (nonatomic, strong) NSDate* date;

@end
