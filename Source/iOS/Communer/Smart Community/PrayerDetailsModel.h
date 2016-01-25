//
//  PrayerDetailsModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"

@interface PrayerDetailsModel : KVCBaseObject

@property (nonatomic) NSTimeInterval date;
@property (nonatomic, strong) NSString* parashaName;
@property (nonatomic, strong) NSMutableArray* prayers;
@property (nonatomic, strong) NSString* holidayStart;
@property (nonatomic, strong) NSString* holidayEnd;
@property (nonatomic, strong) NSMutableArray* smallPrayers;
@property (nonatomic, strong) NSString* communityID;
@property (nonatomic, strong) NSArray* additions;


@end
