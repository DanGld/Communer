//
//  dailyEventsModel.h
//  Smart Community
//
//  Created by oren shany on 8/24/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"


@interface dailyEventsModel : KVCBaseObject

@property (nonatomic, strong) NSString* date;
@property (nonatomic, strong) NSMutableArray* events;
@property (nonatomic, strong) NSMutableArray* activities;
@property (nonatomic, strong) NSMutableArray* prayers;

@end
